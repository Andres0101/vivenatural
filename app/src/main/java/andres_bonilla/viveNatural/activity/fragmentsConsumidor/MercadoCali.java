package andres_bonilla.viveNatural.activity.fragmentsConsumidor;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import andres_bonilla.viveNatural.activity.R;

public class MercadoCali extends Fragment {

    public MercadoCali() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mercado_cali, container, false);

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

        TextView content1 = (TextView) rootView.findViewById(R.id.content1);
        content1.setTypeface(light);

        TextView content2 = (TextView) rootView.findViewById(R.id.content2);
        content2.setTypeface(light);

        TextView content3 = (TextView) rootView.findViewById(R.id.content3);
        content3.setTypeface(light);

        // Inflate the layout for this fragment
        return rootView;
    }
}
