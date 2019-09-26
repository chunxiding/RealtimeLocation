# RealtimeLocation
Android Realtime Location Tracking app with friending functions in JAVA

## Version 1.0
### Database  
Firebase Realtime Database  

Database Structure:  
- PublicLocation: Location info of each user from Google Map API  
- Tokens: Unique token for each user  
- UserInformation:  
    * User: keyed by UID, with UID and name/email  
    * FriendReuqest: pending friend request sending to the user, with sender UID and name/email  
    * AcceptList: accepted friends of the user, with sender UID and name/email  
### App Structure
- Service
    * MyFCMServie: FirebaseMessagingService extension, handles receiving of friend requests sent; notifications differ depending on SDK version.
    * MyLocationReceiver: use paperdb for local storage of logged user info; return realtime location if app open or stored location if app idle.
- Activities
    * MainActivity: handles login page; Dexter for permission checking for location (Prompt); setup UI for HomeActivity.
    * HomeActivity: Friend list with search; onClick event to Tracking Activity.
    * TrackingActivity: connect to GoogleMap API and create a marker based on current location.
    * AllPeopleActivity: activate from sidebar; show all users of app; onClick event for sending friend requests.
    * FriendRequestActivity: activate from sidebar; show friend requests list; accept or cancel requests.
    
