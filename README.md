
# [ShareTitle](https://github.com/Gavin1937/ShareTitle)

[![](https://github.com/Gavin1937/ShareTitle/actions/workflows/docker-image.yml/badge.svg)](https://github.com/Gavin1937/ShareTitle/actions/workflows/docker-image.yml)

[![Pack Head Release](https://github.com/Gavin1937/ShareTitle/actions/workflows/pack-head-release.yml/badge.svg)](https://github.com/Gavin1937/ShareTitle/actions/workflows/pack-head-release.yml)

## Share title & url to custom server

This is a spring boot application which functionally similar to browser bookmark but with REST api to help you manipulate database remotely.

In this project, **sharetitle** is basically **bookmarks** or **plain text of website title & url**

I use it with [Android App HTTP Shortcuts](https://http-shortcuts.rmy.ch/documentation) so I can share websites to server from other apps.


## Requirements

* Java JRE 17 (Java JDK 17 if you want to [Deploy Manually](#deploy-manually))
* **[Optional]** Maven >= 3.8.6
* **[Optional]** Docker latest
* **[Optional]** Sqlite3 for database
* **[Optional]** Python >= 3.8 for all python scripts


## REST API

### Rest API path: `/api/...`

### GET `/api/status`

* Get database status
* Return
```json
{
 "last_update_time":int,
 "sharetitle_count":int,
 "ok":boolean
}
```

### GET `/api/allsharetitles/{limit}?is_visit={I}&reverse={R}`

* Get all sharetitles from database
* Optional Path Parameter:
  * `limit` => integer limiting how many sharetitle returns.
    * If supply negative number or not supply, api will return all sharetitles.
* Optional url query:
  * `is_visit` => integer url query setting if returned sharetitles is visited or not.
    * If set to 0, returns non visited sharetitles
    * If set to 1, returns visited sharetitles
    * Default = -1, returns all sharetitles (non visited and visited)
  * `reverse` => integer url query setting order of returned sharetitles.
    * If set to 1, order in ascending order by id.
    * If set to 0, order in descending order by id.
    * Default = 1 (ascending order).
* Return
```json
{
 "sharetitles": [
  {
   "id": id,
   "title": string,
   "url": string,
   "domain": string,
   "parent_child": int,
   "is_visited": int,
   "time": int (timestamp)
  },
  ...
 ],
 "length": int,
 "ok": boolean
}
```

### GET `/api/sharetitle/{id}`

* Get sharetitle form database specified by `id`
* Path Parameter
  * `id` => integer id exists in database
* Return
```json
{
 "sharetitle": {
  "domain": string,
  "is_visited": int,
  "id": int,
  "time": int,
  "title": string,
  "url": string,
  "parent_child": int
 },
 "ok": boolean
}
```

### POST `/api/sharetitle`

* Add new sharetitle to database.
* This endpoint **only allow Content-Type: text/plain**, be sure to change your header.
* Request Parameter
  * plain text data that can be match by [any regex in parseScript.json](#parsescriptjson)
* Return
```json
{
 "sharetitle": {
  "domain": string,
  "is_visited": int,
  "id": int,
  "time": int,
  "title": string,
  "url": string,
  "parent_child": int
 },
 "ok": boolean
}
```

### DELETE `/api/sharetitle/{id}`

* Delete sharetitle from database specified by `id`.
* Path Parameter:
  * `id` => integer id exists in database.
* Return
```json
{
 "id": int,
 "ok": boolean
}
```

### PUT `/api/sharetitle/{id}`

* Toggle is_visited status of a sharetitle in database specified by `id`.
* Path Parameter:
  * `id` => integer id exists in database.
* Return
```json
{
 "id": int,
 "is_visited": int,
 "time": int,
 "ok": boolean
}
```

### Errors

* Most errors are following this format

```json
{
  "error": "error message",
  "ok": false
}
```

* Note that some error message hasn't been customized (e.g. 500 internal error), so they have spring boot's default error message

```json
{
  "timestamp":"yyyy-mm-ddTHH:MM:ss.SSS+ZONE",
  "status":500,
  "error":"Internal Server Error",
  "message":"...",
  "path":"/path"
}
```


## Configuration

Please configure following files before you deploy or build project.

### config.json

#### **Please use Absolute Path**

```json
{
    "require_auth": boolean,
    "auth_database_path": string,
    "database_path": string,
    "title_parse_script": string,
    "log_path": string,
    "log_level": string,
    "port": int
}
```

* `require_auth`
  * Whether enable authentication in application.
  * If set to true, `auth_database_path` is required.
* `auth_database_path`
  * Absolute Path to authentication database.
  * If path isn't a file, application will try to create one at input path.
* `database_path`
  * Absolute Path to application database.
  * If path isn't a file, application will try to create one at input path.
* `title_parse_script`
  * Absolute Path to [parseScript.json](#parsescriptjson)
* `log_path`
  * Absolute Path to log file
  * If path isn't a file, application will try to create one at input path.
* `log_level`
  * Logging level in string (case insensitive)
    * TRACE
    * DEBUG
    * INFO
    * WARN
    * ERROR
  * This application uses Java LOGBack for logging, for more information about logging level [see here](https://logback.qos.ch/manual/architecture.html#basic_selection)
* `port`
  * Which port does application listen to.
  * Port number must **greater than 0**
  * If Port number is 0 or `port` section does not exists, application will use **default port 8080**


### parseScript.json

This file tells application how to parse plain text data input by user.

```json
[
  {
  	"domain": string,
  	"regex": string,
  	"parent_child": int
  },
  ...
]
```

* This json file contains a list of object, each object parses specific type of input text data
* `domain`
  * A string name of this type of text data.
  * It doesn't have to be the http domain of url
* `regex`
  * Regular Expression to match input text
  * Each `regex` must have 2 groups: first one for title second for url **(order matters)**
  * Example: `^(.*Twitter) - (?!http.*twitter.*status.*)(http.*twitter.*(?!status).*)$`
  * Above `regex` will parse input text of a twitter profile and separate the title and url from it.
* `parent_child`
  * Input text is a parent link (e.g. twitter profile page) or a child link (e.g. twitter tweet page)
  * 0 => Parent
  * 1 => Child
* You can use provided [parseScript.json](./data/parseScript.json) file as your foundation.


## frontend

### frontend path: `/sharetitle`

Simply visit [http://localhost:8080/sharetitle](http://localhost:8080/sharetitle) and start navigating

If you set `require_auth` to true in [config.json](#configjson) you will be redirect to [http://localhost:8080/login](http://localhost:8080/login) with a login frontend.

Register feature does not come with the application, you need to manually edit database.

If you really want a register page, checkout [next section](#enable-register)

Sample code

```
// open database
$ sqlite3 /path/to/sharetitle_auth.sqlite3

// sql to insert new account
INSERT INTO auth VALUES("username", "auth_hash");
```

`auth_hash` in authentication database is calculate by md5 with combination of username and password. (checkout [init_auth_db.sql](./src/main/resources/sql/init_auth_db.sql) for table schema)

Sample python3 code to generate `auth_hash`

```py
from hashlib import md5
print(md5(
    (
      input('Enter username: ').strip().lower() + 
      input('Enter password: ').strip()
    ).encode('ascii')
  ).hexdigest()
)
```

Note that `auth_hash` requires a **lowercase username** combined with a password (no case requirement)

After you put your account in database, you can login in the frontend.


### Add Register Feature

To add register feature, uncomment `register()` function in [AuthManager.java](./src/main/java/Gavin1937/ShareTitle/Util/AuthManager.java) and `postRegister()` function in [WebappController.java](./src/main/java/Gavin1937/ShareTitle/Controller/WebappController.java)

Next, you need to implement a register form as your frontend.

Do a POST request to path `/register` with request parameters `username` and `auth_hash`

You can use [login.jsp](./src/main/webapp/WEB-INF/jsp/login.jsp) and [login.js](./src/main/resources/static/js/login.js) in the project as your foundation.

Finally [rebuild the project](#build)


## Deploy

### Deploy with Docker (Recommend)

Note that application working directory in container is **/app**

Pull image from [DockerHub gavin1937/sharetitle](https://hub.docker.com/r/gavin1937/sharetitle)

```sh
docker pull gavin1937/sharetitle
```

And start container

```sh
docker run -d --restart unless-stopped \
           --name sharetitle \
           -p 8080:8080 \
           -v /path/to/datadir:/app/data \
           gavin1937/sharetitle:latest /container/path/to/config.json
```

* Be sure to add `-v` flag to mount your local filesystem into container.
* Last argument is your [config.json](#configjson) with path in the container.
* When you configure [config.json](#configjson) for container, be sure to use path in the container.
* Highly recommend sticking with working directory **/app**, just put all configuration, log, and database in a local directory and then mount it to **/app/data**.
* You can use provided [docker_config.json](./data/docker_config.json) file as your base.

### Deploy Manually

First, you need to [Build project manully](#build) or [Download the pre-build package](https://github.com/Gavin1937/ShareTitle/releases/download/head/ShareTitle.zip)

Next, unzip the package if you choose pre-build package.

Then, run **target/ShareTitle.jar** file from project's root directory.

You must run jar file **from project's root directory** so spring boot can find all jsp files for the frontend.

```sh
java -jar target/ShareTitle.jar /path/to/your/config.json
```

For linux users, you can use [sharetitle.service](./sharetitle.service) with systemd to help you setup application auto launch at start up.


## Build

This project use maven as build system.

To build application, run

```sh
mvn clean package
```

Or, if you don't have maven installed, you can use portable maven shipped with repository

**Unix system**
```sh
./mvnw clean package
```

**Windows**
```sh
mvnw.cmd clean package
```

Be sure to **setup JAVA_HOME environment variable before build the repository**

You can use [DockerConfigureBuild.py](./DockerConfigureBuild.py) to build project and docker image at once.

