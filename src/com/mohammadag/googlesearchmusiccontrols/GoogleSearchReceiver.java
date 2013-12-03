package com.mohammadag.googlesearchmusiccontrols;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

public class GoogleSearchReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String queryText = intent.getStringExtra(GoogleSearchApi.KEY_QUERY_TEXT);
		if (queryText.contains("song")) {
			if (queryText.contains("what") && !queryText.startsWith("play")) {
				Intent shazam = new Intent("com.shazam.android.intent.actions.START_TAGGING");
				shazam.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(shazam);
			}
		}

		if (queryText.contains("listen to") || queryText.contains("play song")) {
			Intent launchIntent = new Intent("android.media.action.MEDIA_PLAY_FROM_SEARCH");
			String songQuery = queryText.replace("listen to", "");
			songQuery = songQuery.replace("play song", "");
			songQuery = songQuery.replace("by", "");
			launchIntent.putExtra(SearchManager.QUERY, songQuery);
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(launchIntent);
		}

		if (queryText.equals("play") || queryText.equals("play music")) {
			sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY);
		}

		if (queryText.contains("playback") || queryText.contains("music") || queryText.contains("song")) {
			if (queryText.startsWith("resume")) {
				sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY);
			}
			
			if (queryText.startsWith("pause")) {
				sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_PAUSE);
			}		
		}
		if (queryText.startsWith("stop")) {
			if (queryText.contains("playback") || queryText.contains("music")
					|| queryText.contains("playing")) {
				sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_STOP);
			}
		}

		if (queryText.contains("track") || queryText.contains("song")) {
			if (queryText.startsWith("next")) {
				sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_NEXT);
			} else if (queryText.startsWith("previous")) {
				sendMediaKey(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			}
		}
		
		if (queryText.startsWith("volume")) {
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (queryText.contains("up")) {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
		                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			} else if (queryText.contains("down")) {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
		                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
			} else if (queryText.contains("max")) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
			}
		}
	}

	private static void sendMediaKey(Context context, int key) {
		Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
		synchronized (context) {
			i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, key));
			context.sendOrderedBroadcast(i, null);

			i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, key));
			context.sendOrderedBroadcast(i, null);
		}
	}
}
