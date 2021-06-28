package city.windmill.ingameime.client

import city.windmill.ingameime.client.KeyHandler.CombinationKeyState.CombinationKeyAction.*
import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.client.jni.ICommitListener
import org.apache.logging.log4j.LogManager

object IMEHandler {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|IMEHandler")!!

    enum class IMEState {
        DISABLED {
            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState {
                return when (action) {
                    CLICKED -> {
                        TEMPORARY
                    }
                    DOUBLE_CLICKED -> {
                        ENABLED
                    }
                    LONG_PRESS -> this
                }
            }

            override fun onCommit(): IMEState {
                return this //do nothing
            }

            override fun onMouseMove(): IMEState {
                return this //do nothing
            }

            override fun onScreenState(state: ScreenHandler.ScreenState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.NULL_SCREEN,
                    ScreenHandler.ScreenState.SCREEN_OPEN -> {
                        DISABLED
                    }
                    ScreenHandler.ScreenState.SCREEN_DUMMY_EDIT -> {
                        ENABLED
                    }
                }
            }

            override fun onEditState(state: ScreenHandler.ScreenState.EditState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.EditState.NULL_EDIT -> {
                        DISABLED
                    }
                    ScreenHandler.ScreenState.EditState.EDIT_OPEN -> {
                        ENABLED
                    }
                }
            }
        },
        TEMPORARY {
            /**
             * Track if we have committed something
             */
            var hasCommit = false

            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState {
                return when (action) {
                    CLICKED -> {
                        DISABLED
                    }
                    DOUBLE_CLICKED -> {
                        ENABLED
                    }
                    LONG_PRESS -> this
                }
            }

            override fun onCommit(): IMEState {
                hasCommit = true
                //Disable IME when next mouse move && we are not composing
                return this
            }

            /**
             * Disable IME when mouse move && we are not composing && hasCommit
             */
            override fun onMouseMove(): IMEState {
                return if (!OverlayScreen.composing && hasCommit)
                    DISABLED
                else {
                    hasCommit = false
                    this
                }
            }

            override fun onScreenState(state: ScreenHandler.ScreenState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.NULL_SCREEN,
                    ScreenHandler.ScreenState.SCREEN_OPEN -> {
                        DISABLED
                    }
                    ScreenHandler.ScreenState.SCREEN_DUMMY_EDIT -> {
                        ENABLED
                    }
                }
            }

            override fun onEditState(state: ScreenHandler.ScreenState.EditState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.EditState.NULL_EDIT -> {
                        DISABLED
                    }
                    ScreenHandler.ScreenState.EditState.EDIT_OPEN -> {
                        ENABLED
                    }
                }
            }
        },
        ENABLED {
            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState {
                return when (action) {
                    CLICKED -> {
                        DISABLED
                    }
                    DOUBLE_CLICKED -> {
                        ENABLED
                    }
                    LONG_PRESS -> this
                }
            }

            override fun onCommit(): IMEState {
                return this //do nothing
            }

            override fun onMouseMove(): IMEState {
                return this //do nothing
            }

            override fun onScreenState(state: ScreenHandler.ScreenState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.NULL_SCREEN,
                    ScreenHandler.ScreenState.SCREEN_OPEN -> {
                        DISABLED
                    }
                    ScreenHandler.ScreenState.SCREEN_DUMMY_EDIT -> {
                        ENABLED
                    }
                }
            }

            override fun onEditState(state: ScreenHandler.ScreenState.EditState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.EditState.NULL_EDIT -> {
                        DISABLED
                    }
                    ScreenHandler.ScreenState.EditState.EDIT_OPEN -> {
                        ENABLED
                    }
                }
            }
        };

        companion object : ICombinationKeyActionListener, ICommitListener, IScreenStateListener,
            IEditStateListener {
            private var imeState = DISABLED
                set(value) {
                    if (field == value) return
                    LOGGER.trace("IMEState $field -> $value")
                    field = value
                    when (field) {
                        DISABLED -> ExternalBaseIME.State = false
                        TEMPORARY,
                        ENABLED -> ExternalBaseIME.State = true
                    }
                }

            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction) {
                imeState = imeState.onAction(action)
            }

            override fun onScreenState(state: ScreenHandler.ScreenState) {
                imeState = imeState.onScreenState(state)
            }

            override fun onEditState(state: ScreenHandler.ScreenState.EditState) {
                imeState = imeState.onEditState(state)
            }

            override fun onCommit(commit: String): String {
                imeState = imeState.onCommit()
                return commit
            }

            fun onMouseMove() {
                imeState = imeState.onMouseMove()
            }
        }

        abstract fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState
        abstract fun onCommit(): IMEState
        abstract fun onMouseMove(): IMEState
        abstract fun onScreenState(state: ScreenHandler.ScreenState): IMEState
        abstract fun onEditState(state: ScreenHandler.ScreenState.EditState): IMEState
    }
}