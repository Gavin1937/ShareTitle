# REST API

## Rest API path: `/api/...`

## GET `/api/status`

* Get database status
* Return
```json
{
 "last_update_time":int,
 "sharetitle_count":int,
 "sharetitle_visited_count":int,
 "sharetitle_unvisited_count":int,
 "ok":boolean
}
```

## GET `/api/allsharetitles/{limit}?is_visit={I}&reverse={R}`

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

## GET `/api/sharetitle/{id}`

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

## GET `/api/query`

* query specific sharetitle(s) from databse
* optional query paramters
  * **page** => [optional][query parameter] int page of query results.
    * Each page has 50 results, default 0
  * **limit** => [optional][query parameter] int limit of query results in each page.
    * Default limit 50
    * Use -1 for all
  * **order** => [optional][query parameter] string order of result.
    * Can be either "ASC" or "DESC", order by id.
  * **id** => [optional][query parameter] int id of sharetitle.
  * **id_greater_then** => [optional][query parameter] return sharetitle with id greater then this id.
  * **id_greater_eq** => [optional][query parameter] return sharetitle with id greater then & equal to this id.
  * **id_less_then** => [optional][query parameter] return sharetitle with id less then this id.
  * **id_less_eq** => [optional][query parameter] return sharetitle with id less then & equal to this id.
  * **title** => [optional][query parameter] str substring to search in sharetitle's title.
  * **rtitle** => [optional][query parameter] str regex to search in sharetitle's title.
  * **url** => [optional][query parameter] str substring to search in sharetitle's url.
  * **rurl** => [optional][query parameter] str regex to search in sharetitle's url.
  * **domain** => [optional][query parameter] str domain of sharetitle.
  * **parent_child** => [optional][query parameter] int value of sharetitle's parent_child status, either 0 or 1.
    * 0 => parent
    * 1 => child
    * You can set it to "parent" (0) or "child" (1) for readability.
    * You can query both parent and child by supplying string "all".
  * **is_visited** => [optional][query parameter] int value of sharetitle's is_visited status, either 0 or 1.
    * 0 => unvisited
    * 1 => visited
    * You can set it to "unvisited" (0) or "visited" (1) for readability.
    * You can query both visited and unvisited by supplying string "all".
  * **time_until** => [optional][query parameter] int value of sharetitle's time.
    * Integer unix timestamp
    * When querying, this api will search for all sharetitles where sharetitle.time <= time_util
    * You can set it to "now" for current unix timestamp
  * **time_after** => [optional][query parameter] int value of sharetitle's time.
    * Integer unix timestamp
    * When querying, this api will search for all sharetitles where sharetitle.time >= time_after
    * You can set it to "now" for current unix timestamp
* Returns:

```jsonc
{
 "sharetitles": [
  {
   "id": 0,
   "title": "string",
   "url": "string",
   "domain": "string",
   "parent_child": 0,
   "is_visited": 0,
   "time": 0 // unix timestamp
  },
  // ...
 ],
 "length": 0,
 "ok": true
}
```


## POST `/api/sharetitle`

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

## DELETE `/api/sharetitle/{id}`

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

## PUT `/api/sharetitle/{id}`

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

## Errors

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

