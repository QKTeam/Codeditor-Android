package com.example.rr.editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.Stack;


public class Undo {
    int index;
    Stack<Action> history = new Stack<>();
    Stack<Action> historyBack = new Stack<>();
    String TAG = "tag1";
    private Editable editable;
    private EditText editText;
    private boolean flag = false;
    private boolean paste =false;

    public Undo(EditText editText) {
        this.editable = editText.getText();
        this.editText = editText;
    }
	public void setUndoWatcher(){
		editText.addTextChangedListener(new Watcher());
	}

    public void setPaste(){
        this.paste = true;
    }
	
    public final void clearHistory() {
        history.clear();
        historyBack.clear();
    }

    public final void undo() {
        if (history.empty()) return;
        flag = true;
        Action action = history.pop();
        historyBack.push(action);
        if (action.isAdd) {
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        } else {
            editable.insert(action.startCursor, action.actionTarget);
            int st=action.startCursor,ed = action.endCursor+1;
            if(ed>editable.length())ed--;
            editText.setSelection(st,ed);
        }
        flag = false;
        if (!history.empty() && history.peek().index == action.index) {
            undo();
        }
    }

    public final void redo(){
        flag = true;
        Action action = historyBack.pop();
        history.push(action);
        if (action.isAdd) {
            editable.insert(action.startCursor, action.actionTarget);
            editText.setSelection(action.startCursor+1, action.endCursor+1);
        } else {
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        }
        flag = false;
        while (!historyBack.empty() && historyBack.peek().index == action.index)
            redo();
    }
    private class Watcher implements TextWatcher {
        boolean del=false;
        int relativePos = -3;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, false);
                    action.setSelectCount(count);
                    history.push(action);
                    historyBack.clear();
                    char c = charSequence.charAt(charSequence.length()-1);
                    int relativePos_ = s.length() - editText.getSelectionStart();
                    boolean update = c=='\n'|| relativePos_!= relativePos;
                    Log.d("DelUpdate", "beforeTextChanged: update:" + update);
                    if(update || !del) {
                        action.setIndex(++index);
                    }
                    else action.setIndex(index);
                    if( relativePos_!= relativePos){
                       relativePos = relativePos_;
                    }
                }
                del = true;
            }
            else del = false;
        }

        @Override
        public  void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, true);
                    boolean isAdd=false;
                    if(!history.empty())isAdd= history.peek().isAdd;
                    history.push(action);
                    historyBack.clear();
                    int relativePos_ = s.length() - editText.getSelectionStart();
                    char c = charSequence.charAt(charSequence.length()-1);
                    boolean update = (c=='\n'|| relativePos != relativePos_||paste);//||del
                    if(before>0){
                        action.setIndex(index);
                    }
                    else {
                        Log.d(TAG, "onTextChanged: again");
                        if(!update&&isAdd){
                            Log.d(TAG, "onTextChanged: indexNo");
                            action.setIndex(index);
                        }
                        else {
                            action.setIndex(++index);
                            Log.d(TAG, "onTextChanged: index++");
                        }
                    }
                    if(relativePos != relativePos_){
                        relativePos = relativePos_;
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            paste = false;
        }
    }

    private class Action {

        CharSequence actionTarget;
        int startCursor;
        int endCursor;
        boolean isAdd;
        int index;


        public Action(CharSequence actionTar, int startCursor, boolean add) {
            this.actionTarget = actionTar;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.isAdd = add;
        }

        public void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
