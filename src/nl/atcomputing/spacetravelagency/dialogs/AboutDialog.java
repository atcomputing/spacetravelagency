package nl.atcomputing.spacetravelagency.dialogs;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class AboutDialog {

	private static AboutDialog instance;

	private Context context;
	private AlertDialog alertDialog;

	protected AboutDialog() {
		// prevent creating instants using constructor
	}

	static public AboutDialog getInstance(Context context) {
		synchronized (context) {
			if( instance == null ) {
				instance = new AboutDialog();
			}
			instance.setContext(context);
			instance.create();
			return instance;
		}
	}

	public void show() {
		alertDialog.show();
	}

	protected void setContext(Context context) {
		this.context = context;
	}

	private void create() {
		final Sounds sounds = Sounds.getInstance(this.context);

		String versionName = "unknown";
		int versionCode = 0;
		try {
			PackageInfo pInfo = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
			versionName = pInfo.versionName;
			versionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.about);
		builder.setPositiveButton(R.string.close, new
				DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sounds.play(Sounds.Type.ITEMPRESS);
				dialog.dismiss();
			}
		});
		TextView tv = new TextView(this.context);
		tv.setGravity(Gravity.CENTER);
		tv.setPadding(10, 10, 10, 10);
		tv.setLinksClickable(true);
		tv.setAutoLinkMask(Linkify.WEB_URLS);
		tv.setText(Html.fromHtml(versionName + "-" + versionCode + 
				"<br/><br/>" 
				+ this.context.getString(R.string.space_travel_agency_brought_to_you_by_at_computing_)) + 
				"\n" 
				+ this.context.getString(R.string.the_one_stop_linux_shop) +
				"\n"
				+ (this.context.getString(R.string.atcomputingwebsite)));
		builder.setView(tv);

		builder.setNeutralButton(R.string.google_services_license, new
				DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				sounds.play(Sounds.Type.ITEMPRESS);
				showGoogleLicenseDialog();
				dialog.dismiss();
			}
		});

		builder.setNegativeButton(R.string.enroll, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.atcomputingandroidcourseurl)));
				context.startActivity(browserIntent);
			}
		});

		this.alertDialog = builder.create();
	}

	private void showGoogleLicenseDialog() {
		final Sounds sounds = Sounds.getInstance(this.context);
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setPositiveButton(R.string.close,
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				sounds.play(Sounds.Type.ITEMPRESS);
				dialog.dismiss();
			}
		}
				);
		builder.setMessage(
				GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this.context)
				);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
