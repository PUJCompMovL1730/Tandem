import { Component, ViewChild } from '@angular/core';
import { Platform, NavController } from 'ionic-angular';

import { GoogleMaps, GoogleMap, GoogleMapsEvent, LatLng, MarkerOptions, Marker} from '@ionic-native/google-maps';

import { CreateEventPage } from '../create-event/create-event';
import { CreateMarkerPage } from '../create-marker/create-marker';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})

export class HomePage
{
  @ViewChild( 'map' ) element;

  constructor( public navCtrl: NavController, private googleMaps: GoogleMaps, public plat: Platform )
  {

  }

  ngAfterViewInit( )
  {
    this.plat.ready().then( () => {
      this.initMap( );
    })
  }

  initMap( )
  {
    let map: GoogleMap = this.googleMaps.create( this.element.nativeElement );

    map.one( GoogleMapsEvent.MAP_READY ).then( (data: any) => {
      let coordinates: LatLng = new LatLng( 33.6396965, -84.4304574 );

      let position = {
        target: coordinates,
        zoom: 17
      };

      map.animateCamera( position );

      let markerOptions: MarkerOptions = {
        position: coordinates,
        title: 'Haloooo'
      };

      const marker = map.addMarker( markerOptions )
        .then( (marker: Marker ) => {
          marker.showInfoWindow( );
        })
    })
  }

  pushCreateEvent( )
  {
    this.navCtrl.push( CreateEventPage );
  }

  pushCreateMarker( )
  {
    this.navCtrl.push( CreateMarkerPage );
  }
}
