package com.hp.advantage.utils;


import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.UnsupportedRecord;


/**
 * Abstract {@link Activity} for reading NFC messages - both via a tag and via Beam (push)
 *
 * @author Thomas Rorvik Skjolberg
 */

public abstract class NfcReaderActivity extends NfcDetectorActivity {

    private static final String TAG = NfcReaderActivity.class.getName();

    @Override
    public void nfcIntentDetected(Intent intent, String action) {
        Log.d(TAG, "nfcIntentDetected: " + action);

        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (messages != null) {
            NdefMessage[] ndefMessages = new NdefMessage[messages.length];
            for (int i = 0; i < messages.length; i++) {
                ndefMessages[i] = (NdefMessage) messages[i];
            }

            if (ndefMessages.length > 0) {
                // read as much as possible
                Message message = new Message();
                for (int i = 0; i < messages.length; i++) {
                    NdefMessage ndefMessage = (NdefMessage) messages[i];

                    for (NdefRecord ndefRecord : ndefMessage.getRecords()) {
                        try {
                            message.add(Record.parse(ndefRecord));
                        } catch (FormatException e) {
                            // if the record is unsupported or corrupted, keep as unsupported record
                            message.add(UnsupportedRecord.parse(ndefRecord));
                        }
                    }
                }
                readNdefMessage(message);
            } else {
                readEmptyNdefMessage();
            }
        } else {
            readNonNdefMessage();
        }
    }

    /**
     * An NDEF message was read and parsed
     *
     * @param message the message
     */

    protected abstract void readNdefMessage(Message message);

    /**
     * An empty NDEF message was read.
     */

    protected abstract void readEmptyNdefMessage();

    /**
     * Something was read via NFC, but it was not an NDEF message.
     * <p/>
     * Handling this situation is out of scope of this project.
     */

    protected abstract void readNonNdefMessage();

}
