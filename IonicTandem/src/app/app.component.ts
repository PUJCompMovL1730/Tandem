import { Component } from '@angular/core';
import { Platform, NavController } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';

import { ViewChild } from '@angular/core';

import { HomePage } from '../pages/home/home';
import { EventsPage } from '../pages/events/events';
import { MarkersPage } from '../pages/markers/markers';
import { CreateEventPage } from '../pages/create-event/create-event';

@Component({
  templateUrl: 'app.html'
})
export class MyApp 
{
  @ViewChild( 'content' ) navCtrl: NavController
  rootPage:any = HomePage;

  constructor(platform: Platform, statusBar: StatusBar, splashScreen: SplashScreen ) {
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      statusBar.styleDefault();
      splashScreen.hide();
    });
  }

  pushEvents()
  {
    this.navCtrl.setRoot( HomePage );
  }

  pushHome()
  {
    this.navCtrl.push( HomePage );
  }

  pushMarkers()
  {
    this.navCtrl.push( MarkersPage );
  }

  logout()
  {
    
  }
}

