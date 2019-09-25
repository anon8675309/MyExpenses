package org.totschnig.myexpenses.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.totschnig.myexpenses.R;
import org.totschnig.myexpenses.fragment.BudgetFragment;

import androidx.annotation.NonNull;

import static org.totschnig.myexpenses.activity.ManageCategories.ACTION_MANAGE;
import static org.totschnig.myexpenses.provider.DatabaseConstants.KEY_ROWID;

public class BudgetActivity extends CategoryActivity<BudgetFragment> {

  public static final String ACTION_BUDGET = "ACTION_BUDGET";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(getThemeId());
    super.onCreate(savedInstanceState);
    mListFragment.loadBudget(getIntent().getLongExtra(KEY_ROWID,0));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.MANAGE_CATEGORIES_COMMAND) {
      Intent i = new Intent(this, ManageCategories.class);
      i.setAction(ACTION_MANAGE);
      startActivity(i);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @NonNull
  @Override
  public String getAction() {
    return ACTION_BUDGET;
  }

  @Override
  protected int getContentView() {
    return R.layout.activity_budget;
  }

  public static int getBackgroundForAvailable(boolean onBudget, ProtectedFragmentActivity.ThemeType themeType) {
    boolean darkTheme = themeType == ProtectedFragmentActivity.ThemeType.dark;
    return onBudget ?
        (darkTheme ? R.drawable.round_background_income_dark : R.drawable.round_background_income_light) :
        (darkTheme ? R.drawable.round_background_expense_dark : R.drawable.round_background_expense_light);
  }
}
