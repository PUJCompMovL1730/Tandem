package co.edu.javeriana.tandemsquad.tandem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.GlobalTravelAdapter;
import co.edu.javeriana.tandemsquad.tandem.adapters.TravelAdapter;
import co.edu.javeriana.tandemsquad.tandem.adapters.UserNotFriendAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class TravelsGlobalFragment extends Fragment implements GlobalTravelAdapter.OnJoinTravelButtonPressedListener {

  private View rootView;

  private List<Recorrido> travelsList;
  private OnFragmentInteractionListener mListener;
  private ListView travelsListView;
  private GlobalTravelAdapter travelAdapter;

  private ProgressDialog dialog;

  private String currentUserId;
  private Usuario currentUser;
  private FireBaseDatabase fireBaseDatabase;

  public TravelsGlobalFragment() {
    // Required empty public constructor
  }

  public static TravelsGlobalFragment newInstance() {
    TravelsGlobalFragment fragment = new TravelsGlobalFragment();
    //Bundle args = new Bundle();
    //fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_travels_global, container, false);
    travelsListView = (ListView) rootView.findViewById(R.id.travels_global_list_view);
    travelsList = new ArrayList<>();
    new Thread(new Runnable() {
      @Override
      public void run() {
        List<Usuario> users = fireBaseDatabase.getAllUsers();
        for (Usuario usuario : users) {
          if (!currentUserId.equals(usuario.getId())) {
            List<Recorrido> userTravelsList = fireBaseDatabase.getTravels(usuario.getId(), "privacidad", "publico");
            if(userTravelsList != null && !userTravelsList.isEmpty()){
              travelsList.addAll(userTravelsList);
            }
          }
        }
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            updateTravelsAdapter(travelsList);
          }
        });
      }
    }).start();
    return rootView;
  }

  public void updateTravelsAdapter(List<Recorrido> travelsList) {
    travelAdapter = new GlobalTravelAdapter(rootView.getContext(), travelsList, this, currentUserId);
    travelsListView.setAdapter(travelAdapter);
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onJoinTravelButtonPressed(Recorrido selectedTravel) {
    selectedTravel.agregarParticipante(currentUser);
    Snackbar.make(getActivity().getCurrentFocus(), "Te uniste al recorrido", Snackbar.LENGTH_LONG).show();
    fireBaseDatabase.updateTravelParticipants(currentUser, selectedTravel);
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

  public void setCurrentUser(Usuario currentUser) {
    this.currentUser = currentUser;

  }

  public void setCurrentUserId(String currentUserId) {
    this.currentUserId = currentUserId;
  }

  public void setFireBaseDatabase(FireBaseDatabase fireBaseDatabase) {
    this.fireBaseDatabase = fireBaseDatabase;
  }
}
