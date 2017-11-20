import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { IonicApp, IonicErrorHandler, IonicModule } from 'ionic-angular';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';

import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { LoginPage } from '../pages/login/login';
import { EventsPage } from '../pages/events/events';
import { CreateEventPage } from '../pages/create-event/create-event';
import { MarkersPage } from '../pages/markers/markers';
import { CreateMarkerPage } from '../pages/create-marker/create-marker';

import { Geolocation } from '@ionic-native/geolocation';
import { GoogleMaps } from '@ionic-native/google-maps';

import { AngularFireModule } from 'angularfire2';
import { AngularFireAuthModule, AngularFireAuth } from 'angularfire2/auth';

export const FIREBASE_CREDENTIALS = {
  apiKey: "AIzaSyCRyiDWeYOg2RKhwqD_23PCIJX8GlKp1J0",
  authDomain: "tandemapp-2a1f3.firebaseapp.com",
  databaseURL: "https://tandemapp-2a1f3.firebaseio.com",
  projectId: "tandemapp-2a1f3",
  storageBucket: "tandemapp-2a1f3.appspot.com",
  messagingSenderId: "761540755261"
};

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    LoginPage,
    EventsPage,
    CreateEventPage,
    MarkersPage,
    CreateMarkerPage
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(MyApp),
    AngularFireModule.initializeApp( FIREBASE_CREDENTIALS ),
    AngularFireAuthModule
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    LoginPage,
    EventsPage,
    CreateEventPage,
    MarkersPage,
    CreateMarkerPage
  ],
  providers: [
    StatusBar,
    SplashScreen,
    Geolocation,
    GoogleMaps,
    AngularFireAuth,
    {provide: ErrorHandler, useClass: IonicErrorHandler}
  ]
})
export class AppModule {}
