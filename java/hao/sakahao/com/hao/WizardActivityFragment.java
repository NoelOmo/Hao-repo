package hao.sakahao.com.hao;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class WizardActivityFragment extends Fragment {

    TextView wizardText, wizardText2, wizardTitle;

    public WizardActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wizard, container, false);
        wizardText =(TextView) view.findViewById(R.id.wz_description);
        wizardText2 =(TextView) view.findViewById(R.id.wz_description_2);
        wizardTitle = (TextView) view.findViewById(R.id.wz_title);
        Typeface wizardTextFont = (Typeface.createFromAsset(getContext().getAssets(),"fonts/Oswald-Light.ttf"));
        wizardText.setTypeface(wizardTextFont);
        wizardText2.setTypeface(wizardTextFont);
        Typeface wizardTitleFont = (Typeface.createFromAsset(getContext().getAssets(),"fonts/Oswald-Bold.ttf"));
        wizardTitle.setTypeface(wizardTitleFont);
        return view;
    }
}
