package city.windmill.ingameime.client

import city.windmill.ingameime.client.KeyHandler.CombinationKeyState.Companion.onAction
import kotlinx.coroutines.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import java.lang.ref.WeakReference

interface IKeyEventListener {
    fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): Boolean
    fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): Boolean
}

interface ICombinationKeyActionListener {
    fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction)
}

object KeyHandler {
    val hotKey: KeyBinding = KeyBinding(
        "key.ingameime.hotkey",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_HOME,
        "category.ingameime.keybinding"
    )
    
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|KeyHandler")!!
    
    enum class KeyState {
        PENDING_KEY_DOWN {
            override fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyState {
                val longPressRepeat = GlobalScope.launch(start = CoroutineStart.LAZY) {
                    var longPressCounter = 0
                    while (keyState == COUNTING_LONG_PRESS) {
                        longPressCounter++
                        if (longPressCounter % 4 == 0) {
                            MinecraftClient.getInstance().execute {
                                LOGGER.debug("Key long pressed for ${longPressCounter / 2} seconds")
                                //Long Press event every 2 seconds
                                onAction(KeyAction.KEY_LONG_PRESS)
                            }
                        }
                        delay(500)
                    }
                }
                Companion.longPressRepeat = WeakReference(longPressRepeat)
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
        
        companion object : IKeyEventListener {
            var keyState = PENDING_KEY_DOWN
                set(value) {
                    if (field == value && value == COUNTING_LONG_PRESS) return
                    LOGGER.debug("KeyState $field -> $value")
                    field = value
                }
            lateinit var delayLongPress: WeakReference<Job>
            lateinit var longPressRepeat: WeakReference<Job>
            
            override fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): Boolean {
                if (keyCode == hotKey.boundKey.code) {
                    keyState = keyState.onKeyDown(keyCode, scanCode, modifier)
                    return true
                }
                return false
            }
            
            override fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): Boolean {
                if (keyCode == hotKey.boundKey.code) {
                    keyState = keyState.onKeyUp(keyCode, scanCode, modifier)
                    return true
                }
                return false
            }
        }
        
        abstract fun onKeyDown(keyCode: Int, scanCode: Int, modifier: Int): KeyState
        abstract fun onKeyUp(keyCode: Int, scanCode: Int, modifier: Int): KeyState
        
        enum class KeyAction {
            KEY_CLICKED,
            KEY_LONG_PRESS;
        }
        
        interface IKeyActionListener {
            fun onAction(action: KeyAction)
        }
    }
    
    enum class CombinationKeyState {
        PENDING_CLICK {
            override fun onAction(action: KeyState.KeyAction): CombinationKeyState {
                return when (action) {
                    KeyState.KeyAction.KEY_CLICKED -> {
                        delayDoubleClick = WeakReference(GlobalScope.launch {
                            delay(300)
                            combinationKeyState = PENDING_CLICK
                            MinecraftClient.getInstance().execute {
                                LOGGER.debug("${CombinationKeyAction.CLICKED}")
                                IMEHandler.IMEState.onAction(CombinationKeyAction.CLICKED)
                            }
                        })
                        PENDING_DOUBLE_CLICK
                    }
                    KeyState.KeyAction.KEY_LONG_PRESS -> {
                        LOGGER.debug("${CombinationKeyAction.LONG_PRESS}")
                        IMEHandler.IMEState.onAction(CombinationKeyAction.LONG_PRESS)
                        PENDING_CLICK
                    }
                }
            }
        },
        PENDING_DOUBLE_CLICK {
            override fun onAction(action: KeyState.KeyAction): CombinationKeyState {
                when (action) {
                    KeyState.KeyAction.KEY_CLICKED -> {
                        delayDoubleClick.get()?.cancel()
                        LOGGER.debug("${CombinationKeyAction.DOUBLE_CLICKED}")
                        IMEHandler.IMEState.onAction(CombinationKeyAction.DOUBLE_CLICKED)
                    }
                    KeyState.KeyAction.KEY_LONG_PRESS -> {
                        LOGGER.debug("${CombinationKeyAction.LONG_PRESS}")
                        IMEHandler.IMEState.onAction(CombinationKeyAction.LONG_PRESS)
                    }
                }
                return PENDING_CLICK
            }
        };
        
        companion object : KeyState.IKeyActionListener {
            var combinationKeyState = PENDING_CLICK
                set(value) {
                    LOGGER.debug("HotKeyState $field -> $value")
                    field = value
                }
            lateinit var delayDoubleClick: WeakReference<Job>
            
            override fun onAction(action: KeyState.KeyAction) {
                combinationKeyState = combinationKeyState.onAction(action)
            }
        }
        
        abstract fun onAction(action: KeyState.KeyAction): CombinationKeyState
        
        enum class CombinationKeyAction {
            CLICKED,
            DOUBLE_CLICKED,
            LONG_PRESS;
        }
    }
}