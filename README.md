# Movie and Tv suggestions 
This app allows the user to browse a movie/tv catalog and see details for specific crewMembers.

Used libraries:

* Glide for loading images
* Gson for passing data between fragments
* SugarORM for database integration 
* Google's design, appcompat, cardview, recyclerview and palette libraries
* github.com/hdodenhof/CircleImageView for actor's profile page

#### How to build this project
You must create a gradle.proeprties file where you will enetr you API key for themoviedatabase.org

    TMDB_API_KEY = "xxxxxxxxxxx"
  
Also the google-services.json is not publicly available because it contains sensitive data. Because of this you should also remove any firebase-related dependencies in the build.gradle files, since having firebase for your own apk makes no sense.

## Changelog


#### Version 2.0
* Added actor's profile page
* Added Coming Soon tab
* Added cast for each movie

-----------
### Play store
https://play.google.com/store/apps/details?id=com.dcs.crewMembers
