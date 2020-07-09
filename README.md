Original App Design Project - README Template
===

# GymBuddies

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This is an app meant to help people find exercise partners in their area. It is similar to tinder where a person matches with you and talks with you. Users will have preferences for certain workouts and can choose who they want to exercise with.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Health & Fitness / Social
- **Mobile:** You can display pictures on this app and upload directly from the photos taken by your camera's phone. I also plan to include location to show people in the area interested in exercising together.
- **Story:** This app will allow people to look for new partners for exercising, or just let people be more comfortable with exercising.
- **Market:** This app can be for everyone over 18 (I do not want to allow minors to link up with adults). It is mainly for people who want to better themselves healthwise which I would argue is most people in some some sense. It alows people to select the workouts they are most comfortable with.
- **Habit:** People would use this app on average 3-4 times a week. The people on this app would be working out an average of 3-4 times in a week. This allows them to link up with new people when they want
- **Scope:** I think building this app is well within the scope of the 3-4 weeks we have. Tinder uses a complex algorithm to show you people in your area, but this app would just show people based on some sort of priority list based on workout and experience preferences.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can sign up and log in to their profile
* Users can upload a profile picture
* Users can use google maps to indicate a place to meet up with the person
* Users can select preferences for workouts and experience
* Users can view other users preferences in their area
* Users can customize their profile with a short description
* Users will match accurately with other users based on distance and preference
* Open a chatting app to chat with the person
* ...

**Optional Nice-to-have Stories**

* Users can upload videos
* A Nice simplistic UI
* Users can chat within the app
* Users can search for others without having to match
* ...

### 2. Screen Archetypes

* Login Screen
   * User can Login
   * ...
* Registration Screen
   * User can create an account
   * ...
* Matches/Home
    * User can view people who match their preferences
    * User can click on their matches profiles
* Profile (edit)
    * User can view their own information and can edit it
        * Pictures
        * Workout preferences
        * Experience
        * Bio
* Profile
    * User can ONLY view others profile information
* Chats with matches
    * Displays current chats with other users
* Chat Screen
    * User can send messages to the other user

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home/Matches feed
* User Profile tab
* Current Chats with buddies

**Flow Navigation** (Screen to Screen)

* Login Screen
   * -> Home
* Registration Screen
   * -> Home
* Matches/Home
    * -> Profile of other users
* Profile
    * -> Home
    * -> Chat screen with that user
* Profile (edit)
    * -> Users own profile view
* Chats with matches
    * -> Chat Screen
* Chat screen
    * -> Chats with matches

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models
**User**
| Property   | Type                 | Description                                           |
|--------------|------------------------|---------------------------------------------------------|
| userId       | String                 | unique id for user account                              |
| screenName   | String                 | display name for user profile                           |
| description  | String                 | user bio                                                |
| profileImage | File                   | image for a user's profile                              |
| gallery      | Array(File)            | images for a user's gallery                             |
| preferences  | JSON Object            | user's preferences for workout partners                 |
| location     | String                 | user's latitude and longitude                           |
| matches      | Array(Pointer to User) | list of pointer to users that the user has matched with |
| userName     | String                 | unique username for user                                |
| email        | String                 | unique email for user                                   |
| password     | String                 | password for user                                       |
| chats        | Array(Pointer to Chat) | list of pointers to chats with other users |
  
**Chat**
| Property | Type                    | Description                                                           |
|----------|-------------------------|-----------------------------------------------------------------------|
| chatId   | String                  | unique id for chat                                                    |
| users    | Array(Pointer to User)  | list of pointers to users chatting                                    |
| messages | JSON Array(JSON Object) | list of messages, each keeping track of timestamp, contents, and user |
  
  
### Networking
- List of network requests by screen
* Login Screen
   * User can Login
   * ...
* Registration Screen
   * User can create an account
   * ...
* Matches/Home
    * User can view people who match their preferences
    * User can click on their matches profiles
* Profile (edit)
    * User can view their own information and can edit it
        * Pictures
        * Workout preferences
        * Experience
        * Bio
* Profile
    * User can ONLY view others profile information
* Chats with matches
    * Displays current chats with other users
* Chat Screen
    * User can send messages to the other user
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
