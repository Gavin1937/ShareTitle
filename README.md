
# [ShareTitle](https://github.com/Gavin1937/ShareTitle)

[![Docker Image CI](https://github.com/Gavin1937/ShareTitle/actions/workflows/docker-image.yml/badge.svg)](https://github.com/Gavin1937/ShareTitle/actions/workflows/docker-image.yml)

[![Pack heading Release](https://github.com/Gavin1937/ShareTitle/actions/workflows/pack-head-release.yml/badge.svg)](https://github.com/Gavin1937/ShareTitle/actions/workflows/pack-head-release.yml)

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


## [Api Documentation](./doc/ApiDocumentation.md)

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

### This frontend is DEPRECATED, consider my [new frontend](https://github.com/Gavin1937/ShareTitle_react) written in react.

**new frontend doen't support Register Feature**

<details>
<summary>DEPRECATED</summary>

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
print(
  md5(
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

</details>


## Deploy

### Deploy whole ShareTitle project with docker-compose (Recommend)

[check out this repo](https://github.com/Gavin1937/ShareTitle_docker_compose)

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

