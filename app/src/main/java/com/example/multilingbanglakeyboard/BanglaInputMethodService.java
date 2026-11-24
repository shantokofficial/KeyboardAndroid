package com.example.multilingbanglakeyboard;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

public class BanglaInputMethodService extends InputMethodService {

    private enum KeyboardMode { BANGLA, ENGLISH, EMOJI }
    private KeyboardMode currentMode = KeyboardMode.BANGLA;
    private boolean isShifted = false;

    @Override
    public View onCreateInputView() {
        return inflateCurrentKeyboard();
    }

    private View inflateCurrentKeyboard() {
        View view;
        if (currentMode == KeyboardMode.BANGLA) {
            view = getLayoutInflater().inflate(R.layout.keyboard_bangla_multiling, null);
            setupBanglaListeners(view);
        } else if (currentMode == KeyboardMode.ENGLISH) {
            view = getLayoutInflater().inflate(R.layout.keyboard_en_sym, null);
            setupEnglishListeners(view);
        } else {
            view = getLayoutInflater().inflate(R.layout.keyboard_emoji, null);
            setupEmojiListeners(view);
        }
        return view;
    }

    private void setupBanglaListeners(View root) {
        bindAllCharacterButtons(root, new int[]{
                R.id.btnMlShift, R.id.btnMlDelete, R.id.btnMlEmoji,
                R.id.btnMlSymbol, R.id.btnMlLangSwitch, R.id.btnMlSpace,
                R.id.btnMlDot, R.id.btnMlReturn
        });

        View btnDelete = root.findViewById(R.id.btnMlDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> sendBackspace());
        }

        View btnSpace = root.findViewById(R.id.btnMlSpace);
        if (btnSpace != null) {
            btnSpace.setOnClickListener(v -> commitText(" "));
        }

        View btnDot = root.findViewById(R.id.btnMlDot);
        if (btnDot != null) {
            btnDot.setOnClickListener(v -> commitText("."));
        }

        View btnReturn = root.findViewById(R.id.btnMlReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(v -> sendEnterOrAction());
        }

        View btnEmoji = root.findViewById(R.id.btnMlEmoji);
        if (btnEmoji != null) {
            btnEmoji.setOnClickListener(v -> {
                currentMode = KeyboardMode.EMOJI;
                setInputView(inflateCurrentKeyboard());
            });
        }

        View btnLangSwitch = root.findViewById(R.id.btnMlLangSwitch);
        if (btnLangSwitch != null) {
            btnLangSwitch.setOnClickListener(v -> {
                currentMode = KeyboardMode.ENGLISH;
                isShifted = false;
                setInputView(inflateCurrentKeyboard());
            });
        }

        View btnShift = root.findViewById(R.id.btnMlShift);
        if (btnShift != null) {
            btnShift.setOnClickListener(v -> {
                isShifted = !isShifted;
                updateBanglaShiftLabels(root);
            });
        }

        View btnSymbol = root.findViewById(R.id.btnMlSymbol);
        if (btnSymbol != null) {
            btnSymbol.setOnClickListener(v -> commitText("।"));
        }
    }

    private void updateBanglaShiftLabels(View root) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Button) {
                Button b = (Button) child;
                int id = b.getId();
                if (id != R.id.btnMlShift && id != R.id.btnMlDelete &&
                    id != R.id.btnMlEmoji && id != R.id.btnMlSymbol &&
                    id != R.id.btnMlLangSwitch && id != R.id.btnMlSpace &&
                    id != R.id.btnMlDot && id != R.id.btnMlReturn) {
                    String current = b.getText().toString();
                    b.setText(mapBanglaShift(current, isShifted));
                }
            } else if (child instanceof ViewGroup) {
                updateBanglaShiftLabels(child);
            }
        }
    }

    private String mapBanglaShift(String key, boolean shift) {
        if (!shift) {
            switch (key) {
                case "খ": return "ক";
                case "ঘ": return "গ";
                case "ধ": return "দ";
                case "ঝ": return "জ";
                case "ঢ": return "ড";
                case "থ": return "ত";
                case "ছ": return "চ";
                case "ঠ": return "ট";
                case "ফ": return "প";
                case "ভ": return "ব";
                case "ঃ": return "ং";
                case "ঁ": return "়";
                default: return key;
            }
        } else {
            switch (key) {
                case "ক": return "খ";
                case "গ": return "ঘ";
                case "দ": return "ধ";
                case "জ": return "ঝ";
                case "ড": return "ঢ";
                case "ত": return "থ";
                case "চ": return "ছ";
                case "ট": return "ঠ";
                case "প": return "ফ";
                case "ব": return "ভ";
                case "ং": return "ঃ";
                case "়": return "ঁ";
                default: return key;
            }
        }
    }

    private void setupEnglishListeners(View root) {
        bindAllCharacterButtons(root, new int[]{
                R.id.btnEnShift, R.id.btnEnDelete, R.id.btnEnToBangla,
                R.id.btnEnSpace, R.id.btnEnDot, R.id.btnEnReturn
        });

        View btnDelete = root.findViewById(R.id.btnEnDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> sendBackspace());
        }

        View btnSpace = root.findViewById(R.id.btnEnSpace);
        if (btnSpace != null) {
            btnSpace.setOnClickListener(v -> commitText(" "));
        }

        View btnDot = root.findViewById(R.id.btnEnDot);
        if (btnDot != null) {
            btnDot.setOnClickListener(v -> commitText("."));
        }

        View btnReturn = root.findViewById(R.id.btnEnReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(v -> sendEnterOrAction());
        }

        View btnToBangla = root.findViewById(R.id.btnEnToBangla);
        if (btnToBangla != null) {
            btnToBangla.setOnClickListener(v -> {
                currentMode = KeyboardMode.BANGLA;
                isShifted = false;
                setInputView(inflateCurrentKeyboard());
            });
        }

        View btnEnShift = root.findViewById(R.id.btnEnShift);
        if (btnEnShift != null) {
            btnEnShift.setOnClickListener(v -> {
                isShifted = !isShifted;
                updateEnglishCase(root);
            });
        }
    }

    private void updateEnglishCase(View root) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Button) {
                Button b = (Button) child;
                int id = b.getId();
                if (id != R.id.btnEnShift && id != R.id.btnEnDelete &&
                    id != R.id.btnEnToBangla && id != R.id.btnEnSpace &&
                    id != R.id.btnEnDot && id != R.id.btnEnReturn) {
                    String txt = b.getText().toString();
                    b.setText(isShifted ? txt.toUpperCase() : txt.toLowerCase());
                }
            } else if (child instanceof ViewGroup) {
                updateEnglishCase(child);
            }
        }
    }

    private void setupEmojiListeners(View root) {
        bindAllCharacterButtons(root, new int[]{
                R.id.btnEmojiToBangla, R.id.btnEmojiSpace,
                R.id.btnEmojiDelete, R.id.btnEmojiReturn
        });

        View btnToBangla = root.findViewById(R.id.btnEmojiToBangla);
        if (btnToBangla != null) {
            btnToBangla.setOnClickListener(v -> {
                currentMode = KeyboardMode.BANGLA;
                isShifted = false;
                setInputView(inflateCurrentKeyboard());
            });
        }

        View btnSpace = root.findViewById(R.id.btnEmojiSpace);
        if (btnSpace != null) {
            btnSpace.setOnClickListener(v -> commitText(" "));
        }

        View btnDelete = root.findViewById(R.id.btnEmojiDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> sendBackspace());
        }

        View btnReturn = root.findViewById(R.id.btnEmojiReturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(v -> sendEnterOrAction());
        }
    }

    private void bindAllCharacterButtons(View root, int[] excludedIds) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Button) {
                int id = child.getId();
                boolean excluded = false;
                for (int ex : excludedIds) {
                    if (id == ex) {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded) {
                    Button button = (Button) child;
                    button.setOnClickListener(v -> commitText(button.getText().toString()));
                }
            } else if (child instanceof ViewGroup) {
                bindAllCharacterButtons(child, excludedIds);
            }
        }
    }

    private void commitText(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.commitText(text, 1);
        }
    }

    private void sendBackspace() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        }
    }

    private void sendEnterOrAction() {
        InputConnection ic = getCurrentInputConnection();
        EditorInfo ei = getCurrentInputEditorInfo();
        if (ic == null) return;

        if (ei != null && (ei.imeOptions & EditorInfo.IME_MASK_ACTION) != EditorInfo.IME_ACTION_NONE) {
            int action = ei.imeOptions & EditorInfo.IME_MASK_ACTION;
            ic.performEditorAction(action);
        } else {
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
        }
    }
}
