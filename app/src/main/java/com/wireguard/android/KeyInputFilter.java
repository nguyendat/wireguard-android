package com.wireguard.android;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.wireguard.crypto.KeyEncoding;

/**
 * InputFilter for entering WireGuard private/public keys encoded with base64.
 */
class KeyInputFilter implements InputFilter {
    @Override
    public CharSequence filter(final CharSequence source,
                               final int sStart, final int sEnd,
                               final Spanned dest,
                               final int dStart, final int dEnd) {
        SpannableStringBuilder replacement = null;
        int rIndex = 0;
        final int dLength = dest.length();
        for (int sIndex = sStart; sIndex < sEnd; ++sIndex) {
            final char c = source.charAt(sIndex);
            final int dIndex = dStart + (sIndex - sStart);
            // Restrict characters to the base64 character set.
            // Ensure adding this character does not push the length over the limit.
            if (((dIndex + 1 < KeyEncoding.KEY_LENGTH_BASE64 && isAllowed(c)) ||
                    (dIndex + 1 == KeyEncoding.KEY_LENGTH_BASE64 && c == '=')) &&
                    dLength + (sIndex - sStart) < KeyEncoding.KEY_LENGTH_BASE64) {
                ++rIndex;
            } else {
                if (replacement == null)
                    replacement = new SpannableStringBuilder(source, sStart, sEnd);
                replacement.delete(rIndex, rIndex + 1);
            }
        }
        return replacement;
    }

    private boolean isAllowed(final char c) {
        return Character.isLetterOrDigit(c) || c == '+' || c == '/';
    }
}