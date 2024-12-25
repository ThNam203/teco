[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Express.js](https://img.shields.io/badge/Express.js-404D59?style=for-the-badge&logo=express&logoColor=white)](https://expressjs.com/)
[![Node.js](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)](https://nodejs.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
# Teco - Project Management Application for Mobile

An Android application for mobile device developed in [Java](https://www.java.com/), XML for frontend and [ExpressJS](https://expressjs.com/) for backend that is inspired by [Monday](https://monday.com/)

Made by a group of students for their Mobile Application Introductory course at [Vietnam University of Information Technology](https://www.uit.edu.vn/).


## Authors

- [@sen1or](https://github.com/ThNam203)
- [@Bui Duy Phuc](https://github.com/PhuscBui)
- [@Nguyen Thai Dang Khoa](https://github.com/NTDKhoa04)



## Acknowledgements

 - [Monday](https://monday.com/) - Overall UI Design inspiration


## Installation

### Prerequisites
* Git
* [Android Studio](https://developer.android.com/studio)
* Keys for evironmental variables
#### Running on Docker:
* [Docker desktop](https://www.docker.com/products/docker-desktop/)
#### Running on local services
* [MongoDB](https://www.mongodb.com/)
* [NodeJS](https://nodejs.org/)
* [npm](https://www.npmjs.com/) (should come with NodeJS)

### Steps

#### 1. Clone the project

```bash
  git clone https://github.com/ThNam203/teco.git
```

#### 2. Setup the backed
##### 2.1 With docker
- Install [Docker engine](https://docs.docker.com/engine/install/) or [Docker desktop](https://www.docker.com/products/) according to your OS
- Edit the evironmental variables in ```/backend/docker-compose.yaml``` to this:
```bash
  NODE_ENV=
  PORT=
  MONGODB_CONNECT=
  JWT_SECRET=
  AWS_ACCESS_KEY=
  AWS_SECRET_ACCESS_KEY=

```
* Build the containters using ```docker compose -f docker-compose.yaml up --build```

##### 2.2 Using local services
- Install [MongoDB (version 7.0.14)](https://www.mongodb.com/try/download/community)
- Install [NodeJS (version 22.5.1)](https://nodejs.org/en/download/package-manager)
- Navigate to ```/backend``` folder and and run ```npm install```
- Create a ```.env``` file at root:
```bash
  NODE_ENV=<TRUE/FALSE>
  PORT=<from 1 to 65535>
  MONGODB_CONNECT=<your MONGODB_CONNECTION_STRING>
  JWT_SECRET=<your JWT_SECRET>
  AWS_ACCESS_KEY=<your AWS_ACCESS_KEY>
  AWS_SECRET_ACCESS_KEY={your AWS_SECRET_ACCESS_KEY}

```
- Run the backend using ```npm run start```


#### 3. Open the ```/frontend``` folder in Android Studio

#### 4. Setup connection for socket
- Run ```ipconfig``` for Windows, ```ifconfig``` for Mac or ```ip addr``` for Linux to get current IPs.
- Open ```com/worthybitbuilders/squadsense/services/RetrofitServices.java``` and ```com/worthybitbuilders/squadsense/utils/SocketClient.java```
- Change the IP address within the files to your current IP: ```<your_ip_here>:3000```

#### 5. Sync Gradle dependencies

#### 6. Add your Google Map API key at ```AndroidManifest.xml```
```bash
  <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="{YOUR_API_KEY_HERE}" />
```
#### 7. Build & Run app
