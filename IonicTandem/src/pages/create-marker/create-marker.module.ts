import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { CreateMarkerPage } from './create-marker';

@NgModule({
  declarations: [
    CreateMarkerPage,
  ],
  imports: [
    IonicPageModule.forChild(CreateMarkerPage),
  ],
})
export class CreateMarkerPageModule {}
