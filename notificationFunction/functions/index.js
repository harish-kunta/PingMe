'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotificationToDevice = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((data, context)=> {

const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;
 
 
 console.log('we have a notification to send to : ',user_id);
 
if (!data.after.exists()) {
         return console.log('A notification has been deleted from the database :',notification_id);

      }
const fromUser= admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

return fromUser.then(fromUserResult =>{
	
	const from_user_id= fromUserResult.val().from;
	
	console.log('You have new notification from :',from_user_id);
	
	const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
	const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
	
	
	return Promise.all([userQuery,deviceToken]).then(result=>{
		
		const userName = result[0].val();
		const token_id = result[1].val();
		
		const payload={
	 notification:{
		 title : "Friend Request",
		 body : `${userName} has sent you request`,
         icon : "default",
		 
         click_action : "com.harish.hk185080.chatterbox_TARGET_NOTIFICATION"	
         		 
	  },
	  data:{
		  user_id : from_user_id
	  }
     };

   return admin.messaging().sendToDevice(token_id,payload).then(response =>{
	
	
	console.log('This was the notification Feature');
	return 1;
	
   });
		
		
	});
	
	 
 });

 
 
});