package co.edu.javeriana.tandemsquad.tandem;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.TravelAdapter;
import co.edu.javeriana.tandemsquad.tandem.adapters.UserNotFriendAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class TravelsOwnFragment extends Fragment {

  private View rootView;

  private List<Recorrido> travelsList;
  private OnFragmentInteractionListener mListener;
  private ListView travelsListView;
  private TravelAdapter travelAdapter;

  private FireBaseAuthentication fireBaseAuthentication;
  private String currentUserId;
  private FireBaseDatabase fireBaseDatabase;

  public TravelsOwnFragment() {
    // Required empty public constructor
  }

  public static TravelsOwnFragment newInstance() {
    TravelsOwnFragment fragment = new TravelsOwnFragment();
    //Bundle args = new Bundle();
    //fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_travels_published, container, false);
    travelsListView = (ListView) rootView.findViewById(R.id.travels_published_list_view);
    new Thread(new Runnable() {
      @Override
      public void run() {
        travelsList = fireBaseDatabase.getTravels(currentUserId, "estado", "planeado");
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            updateTravelsAdapter(travelsList);
          }
        });
      }
    }
    ).start();
    return rootView;
  }

  public void updateTravelsAdapter(List<Recorrido> travelsList) {
    travelAdapter = new TravelAdapter(rootView.getContext(), travelsList);
    travelsListView.setAdapter(travelAdapter);
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }

  public void setCurrentUserId(String currentUserId) {
    this.currentUserId = currentUserId;
  }

  public void setFireBaseDatabase(FireBaseDatabase fireBaseDatabase) {
    this.fireBaseDatabase = fireBaseDatabase;
  }
}
