import { Component } from '@angular/core';
import { RegistrationRequest } from 'src/app/services/models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerRequest: RegistrationRequest = {email: '', cognome:'', nome:'', password:'', username:''}
  errorMsg: 
}
