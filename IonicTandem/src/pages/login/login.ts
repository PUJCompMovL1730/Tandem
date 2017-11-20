import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';

import { Enterprise } from "../../models/enterpirse.model";
import { AngularFireAuth } from 'angularfire2/auth';

import { HomePage } from '../home/home';

@IonicPage()
@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
})
export class LoginPage 
{
  enterprise = {} as Enterprise;
  email: string;
  password: string;

  constructor(public navCtrl: NavController, public navParams: NavParams, private afAuth: AngularFireAuth ) 
  {
  }

  async login( enterpirse: Enterprise )
  {
    try
    {
      const result = await this.afAuth.auth.signInWithEmailAndPassword( this.email, this.password );

      if( result )
      {
        this.navCtrl.setRoot( HomePage );
      }
      else
      {
        console.log( result );
      }
    }
    catch ( e )
    {
      console.error( e );
    }
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad LoginPage');
  }

}
