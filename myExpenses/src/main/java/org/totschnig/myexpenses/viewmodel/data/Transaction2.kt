package org.totschnig.myexpenses.viewmodel.data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.totschnig.myexpenses.model.*
import org.totschnig.myexpenses.model.PaymentMethod.localizedLabelSqlColumn
import org.totschnig.myexpenses.provider.*
import org.totschnig.myexpenses.provider.DatabaseConstants.*
import org.totschnig.myexpenses.util.AppDirHelper
import org.totschnig.myexpenses.util.enumValueOrDefault
import org.totschnig.myexpenses.util.enumValueOrNull
import org.totschnig.myexpenses.util.epoch2ZonedDateTime
import java.io.File
import java.time.ZonedDateTime

@Parcelize
data class Transaction2(
    val id: Long,
    val date: ZonedDateTime,
    val valueDate: ZonedDateTime,
    val amount: Money,
    val comment: String?,
    val catId: Long?,
    val label: String?,
    val payee: String?,
    val transferPeer: Long?,
    val transferAccount: Long?,
    val accountId: Long,
    val methodId: Long?,
    val methodLabel: String?,
    val crStatus: CrStatus,
    val referenceNumber: String?,
    val currency: CurrencyUnit,
    val pictureUri: Uri?,
    val color: Int?,
    val transferPeerParent: Long?,
    val status: Int,
    val accountLabel: String?,
    val accountType: AccountType?,
    val tagList: String? = null,
    val year: Int,
    val month: Int,
    val week: Int,
    val day: Int
): Parcelable {

    val isSplit: Boolean
        get() = catId == SPLIT_CATID

    val isTransfer: Boolean
        get() = transferPeer != null

    companion object {
        fun projection(context: Context) = arrayOf(
            KEY_ROWID,
            KEY_DATE,
            KEY_VALUE_DATE,
            KEY_AMOUNT,
            KEY_COMMENT,
            KEY_CATID,
            FULL_LABEL,
            KEY_PAYEE_NAME,
            KEY_TRANSFER_PEER,
            KEY_TRANSFER_ACCOUNT,
            KEY_ACCOUNTID,
            KEY_METHODID,
            localizedLabelSqlColumn(context, KEY_METHOD_LABEL) + " AS " + KEY_METHOD_LABEL,
            KEY_CR_STATUS,
            KEY_REFERENCE_NUMBER,
            KEY_CURRENCY,
            KEY_PICTURE_URI,
            "$TRANSFER_PEER_PARENT AS $KEY_TRANSFER_PEER_PARENT",
            KEY_STATUS,
            KEY_TAGLIST,
            KEY_PARENTID,
            "$YEAR AS $KEY_YEAR",
            "${getMonth()} AS $KEY_MONTH",
            "${getWeek()} AS $KEY_WEEK",
            "$DAY AS $KEY_DAY"
        )

        val additionalAggregateColumns = arrayOf(
            KEY_COLOR,
            KEY_ACCOUNT_LABEL,
            KEY_ACCOUNT_TYPE,
            "$IS_SAME_CURRENCY AS $KEY_IS_SAME_CURRENCY"
        )

        val additionGrandTotalColumns = arrayOf(
            KEY_CURRENCY,
            "${getAmountHomeEquivalent(VIEW_EXTENDED)} AS $KEY_EQUIVALENT_AMOUNT"
        )

        fun fromCursor(
            context: Context,
            cursor: Cursor,
            currencyContext: CurrencyContext
        ): Transaction2 {
            val currencyUnit = currencyContext.get(cursor.getString(KEY_CURRENCY))
            val amountRaw = cursor.getLong(KEY_AMOUNT)
            val money = Money(currencyUnit, amountRaw)
            val date: Long = cursor.getLong(KEY_DATE)
            val valueDate: Long = cursor.getLong(KEY_VALUE_DATE)
            val transferPeer = cursor.getLongOrNull(KEY_TRANSFER_PEER)

            return Transaction2(
                id = cursor.getLongOrNull(KEY_ROWID) ?: 0,
                amount = money,
                date = epoch2ZonedDateTime(date),
                valueDate = epoch2ZonedDateTime(valueDate),
                comment = cursor.getStringOrNull(KEY_COMMENT),
                catId = cursor.getLongOrNull( KEY_CATID),
                payee = cursor.getStringOrNull(KEY_PAYEE_NAME),
                methodLabel = cursor.getStringOrNull(KEY_METHOD_LABEL),
                label = cursor.getStringOrNull(KEY_LABEL),
                transferPeer = transferPeer,
                transferAccount = cursor.getLongOrNull(KEY_TRANSFER_ACCOUNT),
                accountId = cursor.getLong(KEY_ACCOUNTID),
                methodId = cursor.getLongOrNull(KEY_METHODID),
                currency = currencyUnit,
                pictureUri = cursor.getStringOrNull(KEY_PICTURE_URI)
                    ?.let { uri ->
                        var parsedUri = Uri.parse(uri)
                        if ("file" == parsedUri.scheme) { // Upgrade from legacy uris
                            parsedUri.path?.let {
                                try {
                                    parsedUri = AppDirHelper.getContentUriForFile(context, File(it))
                                } catch (ignored: IllegalArgumentException) {
                                }
                            }
                        }
                        parsedUri
                    },
                crStatus = enumValueOrDefault(
                    cursor.getString(KEY_CR_STATUS),
                    CrStatus.UNRECONCILED
                ),
                referenceNumber = cursor.getStringOrNull(KEY_REFERENCE_NUMBER),
                accountLabel = cursor.getStringIfExists(KEY_ACCOUNT_LABEL),
                accountType = enumValueOrNull<AccountType>(
                    cursor.getStringIfExists(KEY_ACCOUNT_TYPE),
                ),
                transferPeerParent = cursor.getLongOrNull(KEY_TRANSFER_PEER_PARENT),
                tagList = cursor.getStringOrNull(KEY_TAGLIST),
                color = cursor.getIntIfExists(KEY_COLOR),
                status = cursor.getInt(KEY_STATUS),
                year = cursor.getInt(KEY_YEAR),
                month = cursor.getInt(KEY_MONTH),
                week = cursor.getInt(KEY_WEEK),
                day = cursor.getInt(KEY_DAY)
            )
        }
    }
}
