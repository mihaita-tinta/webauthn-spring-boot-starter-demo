(window.webpackJsonp=window.webpackJsonp||[]).push([[59],{"ct+p":function(t,e,i){"use strict";i.r(e),i.d(e,"HomePageModule",function(){return h});var n=i("ofXK"),o=i("TEn/"),r=i("3Pt+"),c=i("tyNb"),s=i("mrSG"),a=i("fXoL"),b=i("ccyI"),d=i("tk/3");function l(t,e){1&t&&(a.Ob(0,"p"),a.jc(1,' Open the application on the other device and go to Registration. Enter the following code in the field "Add Authenticator Code" and register the authenticator. The new authenticator will be assigned to this account. '),a.Nb())}let u=(()=>{class t{constructor(t,e,i){this.authService=t,this.navCtrl=e,this.httpClient=i,this.secret=null,this.registrationAddToken=null}requestRegisterAdditionalAuthenticator(){this.httpClient.get("registration/add",{responseType:"text",withCredentials:!0}).subscribe(t=>this.registrationAddToken=t)}logout(){return Object(s.a)(this,void 0,void 0,function*(){this.authService.logout().subscribe(()=>this.navCtrl.navigateRoot("/login"))})}ngOnInit(){this.httpClient.get("secret",{responseType:"text",withCredentials:!0}).subscribe(t=>this.secret=t)}}return t.\u0275fac=function(e){return new(e||t)(a.Jb(b.a),a.Jb(o.w),a.Jb(d.a))},t.\u0275cmp=a.Db({type:t,selectors:[["app-home"]],decls:18,vars:3,consts:[["color","primary"],[1,"ion-padding"],["fill","clear",3,"click"],[4,"ngIf"],[1,"ion-text-center"],[1,"ion-margin-top"],[1,"ion-margin-top",3,"click"]],template:function(t,e){1&t&&(a.Ob(0,"ion-header"),a.Ob(1,"ion-toolbar",0),a.Ob(2,"ion-title"),a.jc(3," Protected Area "),a.Nb(),a.Nb(),a.Nb(),a.Ob(4,"ion-content",1),a.Ob(5,"h1"),a.jc(6,"Logged In"),a.Nb(),a.Ob(7,"p"),a.jc(8),a.Nb(),a.Ob(9,"ion-button",2),a.Wb("click",function(){return e.requestRegisterAdditionalAuthenticator()}),a.jc(10,"Register additional Authenticator "),a.Nb(),a.ic(11,l,2,0,"p",3),a.Ob(12,"div",4),a.Ob(13,"code"),a.jc(14),a.Nb(),a.Nb(),a.Ob(15,"div",5),a.Ob(16,"ion-button",6),a.Wb("click",function(){return e.logout()}),a.jc(17,"Logout"),a.Nb(),a.Nb(),a.Nb()),2&t&&(a.zb(8),a.kc(e.secret),a.zb(3),a.bc("ngIf",e.registrationAddToken),a.zb(3),a.kc(e.registrationAddToken))},directives:[o.i,o.s,o.r,o.g,o.d,n.h],styles:["code[_ngcontent-%COMP%]{font-weight:700;font-size:20px}"]}),t})(),h=(()=>{class t{}return t.\u0275mod=a.Hb({type:t}),t.\u0275inj=a.Gb({factory:function(e){return new(e||t)},imports:[[n.b,r.a,o.t,d.b,c.i.forChild([{path:"",component:u}])]]}),t})()}}]);