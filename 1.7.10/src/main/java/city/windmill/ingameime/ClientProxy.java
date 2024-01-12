package city.windmill.ingameime;

import city.windmill.ingameime.mixins.MixinGuiScreen;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ingameime.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class ClientProxy {
    static PreEditCallbackImpl preEditCallbackProxy = null;
    static CommitCallbackImpl commitCallbackProxy = null;
    static CandidateListCallbackImpl candidateListCallbackProxy = null;
    static InputModeCallbackImpl inputModeCallbackProxy = null;

    static PreEditCallback preEditCallback = null;
    static CommitCallback commitCallback = null;
    static CandidateListCallback candidateListCallback = null;
    static InputModeCallback inputModeCallback = null;

    private static void tryLoadLibrary(String libName) {
        if (!IngameIME_Forge.LIBRARY_LOADED)
            try {
                InputStream lib = IngameIME.class.getClassLoader().getResourceAsStream(libName);
                if (lib == null) throw new RuntimeException("Required library resource not exist!");
                Path path = Files.createTempFile("IngameIME-Native", null);
                Files.copy(lib, path, StandardCopyOption.REPLACE_EXISTING);
                System.load(path.toString());
                IngameIME_Forge.LIBRARY_LOADED = true;
                IngameIME_Forge.LOG.info("Library [{}] has loaded!", libName);
            } catch (Throwable e) {
                IngameIME_Forge.LOG.warn("Try to load library [{}] but failed: {}", libName, e.getMessage());
            }
        else
            IngameIME_Forge.LOG.info("Library has loaded, skip loading of [{}]", libName);
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
            IngameIME_Forge.LOG.error("Failed to get window handle: {}", e.getMessage());
            return 0;
        }
    }

    public static void destroyInputCtx() {
        if (IngameIME_Forge.InputCtx != null) {
            IngameIME_Forge.InputCtx.delete();
            IngameIME_Forge.InputCtx = null;
            IngameIME_Forge.LOG.info("InputContext has destroyed!");
        }
    }

    public static void createInputCtx() {
        if (!IngameIME_Forge.LIBRARY_LOADED) return;

        IngameIME_Forge.LOG.info("Using IngameIME-Native: {}", InputContext.getVersion());

        long hWnd = getHwnd();
        if (hWnd != 0) {
            // Once switched to the full screen, we can't back to not UiLess mode, unless restart the game
            if (Minecraft.getMinecraft().isFullScreen())
                Config.UiLess_Windows.set(true);
            API api = Config.API_Windows.getString().equals("TextServiceFramework") ? API.TextServiceFramework : API.Imm32;
            IngameIME_Forge.LOG.info("Using API: {}, UiLess: {}", api, Config.UiLess_Windows.getBoolean());
            IngameIME_Forge.InputCtx = IngameIME.CreateInputContextWin32(hWnd, api, Config.UiLess_Windows.getBoolean());
            IngameIME_Forge.LOG.info("InputContext has created!");
        } else {
            IngameIME_Forge.LOG.info("InputContext could not init as the hWnd is NULL!");
            return;
        }

        preEditCallbackProxy = new PreEditCallbackImpl() {
            @Override
            protected void call(CompositionState arg0, PreEditContext arg1) {
                try {
                    IngameIME_Forge.LOG.info("PreEdit State: {}", arg0);

                    //Hide Indicator when PreEdit start
                    if (arg0 == CompositionState.Begin)
                        IngameIME_Forge.Screen.WInputMode.setActive(false);

                    if (arg1 != null)
                        IngameIME_Forge.Screen.PreEdit.setContent(arg1.getContent(), arg1.getSelStart());
                    else
                        IngameIME_Forge.Screen.PreEdit.setContent(null, -1);
                } catch (Throwable e) {
                    IngameIME_Forge.LOG.error(e.getMessage());
                }
            }
        };
        preEditCallbackProxy.swigReleaseOwnership();
        preEditCallback = new PreEditCallback(preEditCallbackProxy);
        commitCallbackProxy = new CommitCallbackImpl() {
            @Override
            protected void call(String arg0) {
                try {
                    IngameIME_Forge.LOG.info("Commit: {}", arg0);
                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                    if (screen != null) {
                        for (char c : arg0.toCharArray()) {
                            ((MixinGuiScreen) screen).callKeyTyped(c, Keyboard.KEY_NONE);
                        }
                    }
                } catch (Throwable e) {
                    IngameIME_Forge.LOG.error(e.getMessage());
                }
            }
        };
        commitCallbackProxy.swigReleaseOwnership();
        commitCallback = new CommitCallback(commitCallbackProxy);
        candidateListCallbackProxy = new CandidateListCallbackImpl() {
            @Override
            protected void call(CandidateListState arg0, CandidateListContext arg1) {
                try {
                    if (arg1 != null)
                        IngameIME_Forge.Screen.CandidateList.setContent(new ArrayList<>(arg1.getCandidates()), arg1.getSelection());
                    else
                        IngameIME_Forge.Screen.CandidateList.setContent(null, -1);
                } catch (Throwable e) {
                    IngameIME_Forge.LOG.error(e.getMessage());
                }
            }
        };
        candidateListCallbackProxy.swigReleaseOwnership();
        candidateListCallback = new CandidateListCallback(candidateListCallbackProxy);
        inputModeCallbackProxy = new InputModeCallbackImpl() {
            @Override
            protected void call(InputMode arg0) {
                try {
                    IngameIME_Forge.Screen.WInputMode.setMode(arg0);
                } catch (Throwable e) {
                    IngameIME_Forge.LOG.error(e.getMessage());
                }
            }
        };
        inputModeCallbackProxy.swigReleaseOwnership();
        inputModeCallback = new InputModeCallback(inputModeCallbackProxy);

        IngameIME_Forge.InputCtx.setCallback(preEditCallback);
        IngameIME_Forge.InputCtx.setCallback(commitCallback);
        IngameIME_Forge.InputCtx.setCallback(candidateListCallback);
        IngameIME_Forge.InputCtx.setCallback(inputModeCallback);

        // Free unused native object
        System.gc();
    }

    private static void loadLibrary() {
        boolean isWindows = LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_WINDOWS;

        if (!isWindows) {
            IngameIME_Forge.LOG.info("Unsupported platform: {}", LWJGLUtil.getPlatformName());
            return;
        }

        tryLoadLibrary("IngameIME_Java-arm64.dll");
        tryLoadLibrary("IngameIME_Java-x64.dll");
        tryLoadLibrary("IngameIME_Java-x86.dll");

        if (!IngameIME_Forge.LIBRARY_LOADED) {
            IngameIME_Forge.LOG.error("Unsupported arch: {}", System.getProperty("os.arch"));
        }
    }

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        loadLibrary();
        createInputCtx();
    }

    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(IngameIME_Forge.KeyBind);
    }
}
