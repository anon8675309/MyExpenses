package org.totschnig.myexpenses.viewmodel.data

import android.database.Cursor
import org.totschnig.myexpenses.provider.DatabaseConstants
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_SECOND_GROUP
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_SUM_EXPENSES
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_SUM_INCOME
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_SUM_TRANSFERS
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_YEAR
import org.totschnig.myexpenses.provider.getInt
import org.totschnig.myexpenses.provider.getLong

/**
 * maps header to an array that holds an array of following sums:
 * [0] incomeSum
 * [1] expenseSum
 * [2] transferSum
 * [3] previousBalance
 * [4] delta (incomSum - expenseSum + transferSum)
 * [5] interimBalance
 * [6] mappedCategories
 *
 * long previousBalance = mAccount.openingBalance.getAmountMinor();
do {
long sumIncome = c.getLong(columnIndexGroupSumIncome);
long sumExpense = c.getLong(columnIndexGroupSumExpense);
long sumTransfer = c.getLong(columnIndexGroupSumTransfer);
long delta = sumIncome + sumExpense + sumTransfer;
long interimBalance = previousBalance + delta;
long mappedCategories = c.getLong(columnIndexGroupMappedCategories);
headerData.put(calculateHeaderId(c.getInt(columnIndexGroupYear), c.getInt(columnIndexGroupSecond)),
new Long[]{sumIncome, sumExpense, sumTransfer, previousBalance, delta, interimBalance, mappedCategories});
previousBalance = interimBalance;
} while (c.moveToNext());
 */

data class HeaderData(
    val incomeSum: Long,
    val expenseSum: Long,
    val transferSum: Long,
    val previousBalance: Long,
    val mappedCategories: Boolean
) {
    val delta: Long = incomeSum + expenseSum + transferSum
    val interimBalance = previousBalance + delta

    companion object {
        fun fromSequence(openingBalance: Long, sequence: Sequence<Cursor>) = buildMap {
            var previousBalance = openingBalance
            for (cursor in sequence) {
                val value = rowFromCursor(previousBalance, cursor)
                put(calculateGroupId(cursor.getInt(KEY_YEAR), cursor.getInt(KEY_SECOND_GROUP)), value)
                previousBalance = value.interimBalance
            }
        }

        private fun calculateGroupId(year: Int, second: Int) = year * 1000 + second

        fun calculateGroupId(transaction: Transaction2) =
            calculateGroupId(transaction.year, transaction.month)

        private fun rowFromCursor(previousBalance: Long, cursor: Cursor) = HeaderData(
            incomeSum = cursor.getLong(KEY_SUM_INCOME),
            expenseSum = cursor.getLong(KEY_SUM_EXPENSES),
            transferSum = cursor.getLong(KEY_SUM_TRANSFERS),
            previousBalance = previousBalance,
            mappedCategories = cursor.getLong(DatabaseConstants.KEY_MAPPED_CATEGORIES) > 0
        )
    }
}