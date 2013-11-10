package com.code44.finance.ui.settings.lock;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.ui.settings.lock.LockPatternView.Cell;
import com.code44.finance.ui.settings.lock.LockPatternView.DisplayMode;
import com.code44.finance.ui.settings.lock.LockPatternView.OnPatternListener;

import java.util.List;

public class LockPatternFragment extends LockFragment implements OnPatternListener
{
    private TextView lockPattern_TV;
    private LockPatternView lockPattern_V;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_lock_pattern, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        lockPattern_TV = (TextView) view.findViewById(R.id.lockPattern_TV);
        lockPattern_V = (LockPatternView) view.findViewById(R.id.lockPattern_V);

        // Setup
        lockPattern_V.setOnPatternListener(this);
    }

    // LockFragment
    // --------------------------------------------------------------------------------------------------------------------------------

    protected void updateViewsNew()
    {
        lockPattern_V.clearPattern();
        lockPattern_V.setDisplayMode(DisplayMode.Correct);
        lockPattern_TV.setText(R.string.lock_pattern_new);
    }

    @Override
    protected void updateViewsConfirmNew()
    {
        lockPattern_V.clearPattern();
        lockPattern_V.setDisplayMode(DisplayMode.Correct);
        lockPattern_TV.setText(R.string.lock_pattern_confirm_new);
    }

    @Override
    protected void updateViewsCreated()
    {
        lockPattern_V.setDisplayMode(DisplayMode.Correct);
        lockPattern_TV.setText(R.string.lock_pattern_created);
    }

    @Override
    protected void updateViewsCompare()
    {
        lockPattern_V.clearPattern();
        lockPattern_V.setDisplayMode(DisplayMode.Correct);
        lockPattern_TV.setText(R.string.lock_pattern_compare);
    }

    @Override
    protected void updateViewsError(int error)
    {
        lockPattern_V.setDisplayMode(DisplayMode.Wrong);
        switch (error)
        {
            case ERROR_NEW:
                lockPattern_TV.setText(R.string.lock_pattern_error_new);
                break;

            case ERROR_COMPARE:
            case ERROR_CONFIRM_NEW:
                lockPattern_TV.setText(R.string.lock_pattern_error_compare);
                break;
        }
    }

    // Private methods
    // --------------------------------------------------------------------------------------------------------------------------------

    private String createLockCode(List<Cell> pattern)
    {
        if (pattern.size() < 4)
            return null;

        return LockPatternUtils.patternToSha1(pattern);
    }

    // OnPatternListener
    // --------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onPatternStart()
    {
        if (getLockType() != LT_COMPARE)
            lockPattern_TV.setText(R.string.lock_pattern_new_in_progress);
    }

    @Override
    public void onPatternDetected(List<Cell> pattern)
    {
        final String newCode = createLockCode(pattern);
        if (TextUtils.isEmpty(newCode))
        {
            if (getLockType() == LT_NEW)
                updateViewsError(ERROR_NEW);
            else if (getLockType() == LT_CONFIRM_NEW)
                updateViewsError(ERROR_CONFIRM_NEW);
            else
                updateViewsError(ERROR_COMPARE);
        }
        else
        {
            onCodeFromUser(newCode);
        }
    }

    @Override
    public void onPatternCleared()
    {
        updateViewsForLockType();
    }

    // Instance
    // --------------------------------------------------------------------------------------------------------------------------------

    public static LockPatternFragment newInstance(int lockType, String currentCode)
    {
        final LockPatternFragment f = new LockPatternFragment();
        f.setArguments(makeArgs(lockType, currentCode));
        return f;
    }
}