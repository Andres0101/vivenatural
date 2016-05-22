package andres_bonilla.viveNatural.activity.fragmentsConsumidor;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import andres_bonilla.viveNatural.activity.R;

public class Asoproorganicos extends Fragment {

    public Asoproorganicos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asoproorganico, container, false);

        Typeface light = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface regular = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setTypeface(regular);

        TextView content = (TextView) rootView.findViewById(R.id.content);
        content.setTypeface(light);

        // Inflate the layout for this fragment
        return rootView;
    }
}
