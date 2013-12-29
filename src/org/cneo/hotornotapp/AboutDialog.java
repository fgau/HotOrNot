package org.cneo.hotornotapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutDialog extends DialogFragment {
	Button mButton;
	onSubmitListener mListener;
	String text = "";
	
	interface onSubmitListener {
		void setOnSubmitListener(String arg);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
//		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.about_dialog);
		dialog.setTitle("About HotOrNot");
//		dialog.getWindow().setBackgroundDrawable(
//				new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
		mButton = (Button) dialog.findViewById(R.id.button1);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return dialog;
	}
}  