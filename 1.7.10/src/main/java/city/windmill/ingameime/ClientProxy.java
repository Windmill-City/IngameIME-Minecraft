package city.windmill.ingameime;

import city.windmill.ingameime.gui.OverlayScreen;
import city.windmill.ingameime.mixins.MixinGuiScreen;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ingameime.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static city.windmill.ingameime.IngameIME_Forge.LOG;
import static org.lwjgl.input.Keyboard.KEY_HOME;

public class ClientProxy extends CommonProxy {
    public static boolean LIBRARY_LOADED = false;
    public static InputContext InputCtx = null;
    public static OverlayScreen Screen = new OverlayScreen();
    public static KeyBinding KeyBind = new KeyBinding("ingameime.key.desc", KEY_HOME, "IngameIME");
    public static boolean IsToggledManually = false;
    static PreEditCallbackImpl preEditCallbackProxy = null;
    static CommitCallbackImpl commitCallbackProxy = null;
    static CandidateListCallbackImpl candidateListCallbackProxy = null;
    static InputModeCallbackImpl inputModeCallbackProxy = null;

    static PreEditCallback preEditCallback = null;
    static CommitCallback commitCallback = null;
    static CandidateListCallback candidateListCallback = null;
    static InputModeCallback inputModeCallback = null;
    private boolean IsKeyDown = false;

    private static void tryLoadLibrary(String libName) {
        if (!LIBRARY_LOADED)
            try {
                InputStream lib = IngameIME.class.getClassLoader().getResourceAsStream(libName);
                if (lib == null) throw new RuntimeException("Required library resource not exist!");
                Path path = Files.createTempFile("IngameIME-Native", null);
                Files.copy(lib, path, StandardCopyOption.REPLACE_EXISTING);
                System.load(path.toString());
                LIBRARY_LOADED = true;
                LOG.info("Library [{}] has loaded!", libName);
            } catch (Throwable e) {
                LOG.warn("Try to load library [{}] but failed: {}", libName, e.getMessage());
            }
        else
            LOG.info("Library has loaded, skip loading of [{}]", libName);
    }

    private static long getHwnd() {
        //long org.lwjgl.opengl.WindowsDisplay.getHwnd()
        try {
            Method methImpl = Display.class.getDeclaredMethod("getImplementation");
            methImpl.setAccessible(true); //Make it accessible, since it is private
            Object impl = methImpl.invoke(null); //Static with no parameters, type not visible anyway so keep as object
            Class<?> clsWinDisplay = Class.forName("org.lwjgl.opengl.WindowsDisplay"); //Not visible, so can't use constant WindowsDisplay.class
            if (!clsWinDisplay.isInstance(impl))
                throw new Exception("The current platform must be Windows!"); //Throw on non-windows host
            Method methHwnd = clsWinDisplay.getDeclaredMethod("getHwnd");
            methHwnd.setAccessible(true);
            return (Long) methHwnd.invoke(impl);
        } catch (Throwable e) {
            LOG.error("Failed to get window handle: {}", e.getMessage());
            return 0;
        }
    }

    public static void destroyInputCtx() {
        if (InputCtx != null) {
            InputCtx.delete();
            InputCtx = null;
            LOG.info("InputContext has destroyed!");
        }
    }

    public static void createInputCtx() {
        if (!LIBRARY_LOADED) return;

        LOG.info("Using IngameIME-Native: {}", InputContext.getVersion());

        long hWnd = getHwnd();
        if (hWnd != 0) {
            // Once switched to the full screen, we can't back to not UiLess mode, unless restart the game
            if (Minecraft.getMinecraft().isFullScreen())
                Config.UiLess_Windows.set(true);
            API api = Config.API_Windows.getString().equals("TextServiceFramework") ? API.TextServiceFramework : API.Imm32;
            LOG.info("Using API: {}, UiLess: {}", api, Config.UiLess_Windows.getBoolean());
            InputCtx = IngameIME.CreateInputContextWin32(hWnd, api, Config.UiLess_Windows.getBoolean());
            LOG.info("InputContext has created!");
        } else {
            LOG.info("InputContext could not init as the hWnd is NULL!");
            return;
        }

        preEditCallbackProxy = new PreEditCallbackImpl() {
            @Override
            protected void call(CompositionState arg0, PreEditContext arg1) {
                try {
                    LOG.info("PreEdit State: {}", arg0);

                    //Hide Indicator when PreEdit start
                    if (arg0 == CompositionState.Begin)
                        Screen.WInputMode.setActive(false);

                    if (arg1 != null)
                        Screen.PreEdit.setContent(arg1.getContent(), arg1.getSelStart());
                    else
                        Screen.PreEdit.setContent(null, -1);
                } catch (Throwable e) {
                    LOG.error(e.getMessage());
                }
            }
        };
        preEditCallback = new PreEditCallback(preEditCallbackProxy);
        commitCallbackProxy = new CommitCallbackImpl() {
            @Override
            protected void call(String arg0) {
                try {
                    LOG.info("Commit: {}", arg0);
                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                    if (screen != null) {
                        // NEI Integration
                        if (Loader.isModLoaded("NotEnoughItems") && GuiContainerManager.getManager() != null) {
                            for (char c : arg0.toCharArray()) {
                                GuiContainerManager.getManager().keyTyped(c, Keyboard.KEY_NONE);
                            }
                        }

                        // Normal Minecraft Guis
                        for (char c : arg0.toCharArray()) {
                            ((MixinGuiScreen) screen).callKeyTyped(c, Keyboard.KEY_NONE);
                        }
                    }
                } catch (Throwable e) {
                    LOG.error(e.getMessage());
                }
            }
        };
        commitCallback = new CommitCallback(commitCallbackProxy);
        candidateListCallbackProxy = new CandidateListCallbackImpl() {
            @Override
            protected void call(CandidateListState arg0, CandidateListContext arg1) {
                try {
                    if (arg1 != null)
                        Screen.CandidateList.setContent(new ArrayList<>(arg1.getCandidates()), arg1.getSelection());
                    else
                        Screen.CandidateList.setContent(null, -1);
                } catch (Throwable e) {
                    LOG.error(e.getMessage());
                }
            }
        };
        candidateListCallback = new CandidateListCallback(candidateListCallbackProxy);
        inputModeCallbackProxy = new InputModeCallbackImpl() {
            @Override
            protected void call(InputMode arg0) {
                try {
                    Screen.WInputMode.setMode(arg0);
                } catch (Throwable e) {
                    LOG.error(e.getMessage());
                }
            }
        };
        inputModeCallback = new InputModeCallback(inputModeCallbackProxy);

        InputCtx.setCallback(preEditCallback);
        InputCtx.setCallback(commitCallback);
        InputCtx.setCallback(candidateListCallback);
        InputCtx.setCallback(inputModeCallback);

        // Free unused native object
        System.gc();
    }

    private static void loadLibrary() {
        boolean isWindows = LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_WINDOWS;

        if (!isWindows) {
            LOG.info("Unsupported platform: {}", LWJGLUtil.getPlatformName());
            return;
        }

        tryLoadLibrary("IngameIME_Java-arm64.dll");
        tryLoadLibrary("IngameIME_Java-x64.dll");
        tryLoadLibrary("IngameIME_Java-x86.dll");

        if (!LIBRARY_LOADED) {
            LOG.error("Unsupported arch: {}", System.getProperty("os.arch"));
        }
    }

    public static boolean getActivated() {
        if (InputCtx != null) {
            return InputCtx.getActivated();
        }
        return false;
    }

    public static void setActivated(boolean activated) {
        if (InputCtx != null && getActivated() != activated) {
            InputCtx.setActivated(activated);
            if (!activated) IsToggledManually = false;
            LOG.info("InputMethod activated: {}", activated);
        }
    }

    public static void toggleInputMethod() {
        setActivated(!getActivated());
    }

    @SubscribeEvent
    public void onRenderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        ClientProxy.Screen.draw();

        if (Keyboard.isKeyDown(ClientProxy.KeyBind.getKeyCode())) {
            IsKeyDown = true;
        } else if (IsKeyDown) {
            IsKeyDown = false;
            ClientProxy.IsToggledManually = true;
            ClientProxy.toggleInputMethod();
            LOG.info("Toggled by keybinding");
        }

        if (Config.TurnOffOnMouseMove.getBoolean())
            if (ClientProxy.IsToggledManually && (Mouse.getDX() > 0 || Mouse.getDY() > 0)) {
                ClientProxy.setActivated(false);
                LOG.info("Turned off by mouse move");
            }
    }

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        ClientRegistry.registerKeyBinding(KeyBind);
        loadLibrary();
        createInputCtx();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
