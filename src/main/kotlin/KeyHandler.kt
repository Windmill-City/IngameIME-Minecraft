import kotlinx.coroutines.*
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import java.lang.ref.WeakReference

interface IKeyEventListener {
    fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyHandler.KeyState
    fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): KeyHandler.KeyState
}

interface IKeyActionListener {
    fun onAction(action: KeyHandler.KeyState.KeyAction): KeyHandler.HotKeyState
}

object KeyHandler : IKeyEventListener, IKeyActionListener {
    val hotKey: KeyBinding = KeyBinding(
        "key.ingameime.hotkey",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_HOME,
        "category.ingameime.keybinding"
    )
    
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|KeyHandler")!!
    
    var keyState = KeyState.PENDING_KEY_DOWN
        set(value) {
            if (field == value && value == KeyState.COUNTING_LONG_PRESS) return
            LOGGER.debug("KeyState $field -> $value")
            field = value
        }
    var hotKeyState = HotKeyState.PENDING_CLICK
        set(value) {
            LOGGER.debug("HotKeyState $field -> $value")
            field = value
        }
    
    enum class KeyState : IKeyEventListener {
        PENDING_KEY_DOWN {
            override fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                val longPressRepeat = GlobalScope.launch(start = CoroutineStart.LAZY) {
                    var longPressCounter = 0
                    while (keyState == COUNTING_LONG_PRESS) {
                        longPressCounter++
                        if (longPressCounter % 4 == 0) {
                            LOGGER.debug("Key long pressed for ${longPressCounter / 2} seconds")
                            //Long Press event every 2 seconds
                            onAction(KeyAction.KEY_LONG_PRESS)
                        }
                        delay(500)
                    }
                }
                KeyState.longPressRepeat = WeakReference(longPressRepeat)
                delayLongPress = WeakReference(GlobalScope.launch {
                    delay(500)
                    keyState = COUNTING_LONG_PRESS
                    LOGGER.debug("Long press repeat start")
                    longPressRepeat.start()
                })
                return PENDING_KEY_UP
            }
            
            override fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                //do nothing
                return this
            }
        },
        
        PENDING_KEY_UP {
            override fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                //do nothing
                return this
            }
            
            override fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                delayLongPress.get()?.cancel()
                longPressRepeat.get()?.cancel() //may have started
                LOGGER.debug("${KeyAction.KEY_CLICKED}")
                onAction(KeyAction.KEY_CLICKED)
                return PENDING_KEY_DOWN
            }
        },
        
        COUNTING_LONG_PRESS {
            override fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                //do nothing
                return this
            }
            
            override fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                longPressRepeat.get()?.cancel()
                return PENDING_KEY_DOWN
            }
        };
        
        companion object {
            lateinit var delayLongPress: WeakReference<Job>
            lateinit var longPressRepeat: WeakReference<Job>
        }
        
        enum class KeyAction {
            KEY_CLICKED,
            KEY_LONG_PRESS;
        }
    }
    
    enum class HotKeyState : IKeyActionListener {
        PENDING_CLICK {
            override fun onAction(action: KeyState.KeyAction): HotKeyState {
                return when (action) {
                    KeyState.KeyAction.KEY_CLICKED -> {
                        delayDoubleClick = WeakReference(GlobalScope.launch {
                            delay(500)
                            hotKeyState = PENDING_CLICK
                            LOGGER.debug("${HotKeyAction.CLICKED}")
                            IMEHandler.onAction(HotKeyAction.CLICKED)
                        })
                        PENDING_DOUBLE_CLICK
                    }
                    KeyState.KeyAction.KEY_LONG_PRESS -> {
                        LOGGER.debug("${HotKeyAction.LONG_PRESS}")
                        IMEHandler.onAction(HotKeyAction.LONG_PRESS)
                        PENDING_CLICK
                    }
                }
            }
        },
        PENDING_DOUBLE_CLICK {
            override fun onAction(action: KeyState.KeyAction): HotKeyState {
                return when (action) {
                    KeyState.KeyAction.KEY_CLICKED -> {
                        delayDoubleClick.get()?.cancel()
                        LOGGER.debug("${HotKeyAction.DOUBLE_CLICKED}")
                        IMEHandler.onAction(HotKeyAction.DOUBLE_CLICKED)
                        PENDING_CLICK
                    }
                    KeyState.KeyAction.KEY_LONG_PRESS -> {
                        LOGGER.debug("${HotKeyAction.LONG_PRESS}")
                        IMEHandler.onAction(HotKeyAction.LONG_PRESS)
                        PENDING_CLICK
                    }
                }
            }
        };
        
        companion object {
            lateinit var delayDoubleClick: WeakReference<Job>
        }
        
        enum class HotKeyAction {
            CLICKED,
            DOUBLE_CLICKED,
            LONG_PRESS;
        }
    }
    
    override fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
        if (keyCode == hotKey.boundKey.code)
            keyState = keyState.onKeyDown(keyCode, scanCode, modifier)
        return keyState
    }
    
    override fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
        if (keyCode == hotKey.boundKey.code)
            keyState = keyState.onKeyUp(keyCode, scanCode, modifier)
        return keyState
    }
    
    override fun onAction(action: KeyState.KeyAction): HotKeyState {
        hotKeyState = hotKeyState.onAction(action)
        return hotKeyState
    }
}