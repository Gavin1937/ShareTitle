
globalThis.websiteTableDOM = null;
globalThis.filteredWebsiteCount = 0;

// 0: not sort by id, 1: ascending, 2: descending
globalThis.sortByIdStat = 0;
// 0: not sort by time, 1: ascending, 2: descending
globalThis.sortByTimeStat = 0;


function collectWebsites()
{
    globalThis.websiteTableDOM = document.createElement("tbody");
    for (c of document.querySelector("tbody").children)
        globalThis.websiteTableDOM.appendChild(c.cloneNode(true));
    globalThis.filteredWebsiteCount = globalThis.websiteTableDOM.children.length-1;
}

function postSharetitle()
{
    var data = document.getElementById("data").value;
    
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/sharetitle", true);
    xhr.setRequestHeader("Content-Type", "text/plain");
    
    // update page after response
    xhr.onreadystatechange = function(responseText) {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            var jresp = JSON.parse(responseText.target.response);
            if (xhr.status == 200 && jresp.ok == true) {
                
                // create new sharetitle html
                var link_innerHTML =
                    jresp.sharetitle.domain +
                    ((jresp.sharetitle.parent_child == "0")?"parent":"child");
                var visitStatus_innerHTML =
                    ((jresp.sharetitle.is_visited == "0")?"unvisited":"visited");
                var visitStatus_color =
                    ((jresp.sharetitle.is_visited == "0")?"red":"green");
                
                var html_str = ```
                <tr id="${jresp.sharetitle.id}">
                    <th class="id" scope="rowgroup">${jresp.sharetitle.id}</th>
                    <th class="title">${jresp.sharetitle.title}</th>
                    <th class="link" domain="${jresp.sharetitle.domain}" parentChild="${jresp.sharetitle.parent_child}">
                        <a href="${jresp.sharetitle.url}">${link_innerHTML}</a>
                    </th>
                    <th class="visitStatus" visited="${jresp.sharetitle.is_visited}">
                        <a onclick="toggleVisited('${jresp.sharetitle.id}');" style="color:${visitStatus_color};">${visitStatus_innerHTML}</a>
                    </th>
                    <th class="time" time="${jresp.sharetitle.time}"></th>
                    <script>beautifyRow("${jresp.sharetitle.id}");</script>
                </tr>
                ```;
                var new_row = new DOMParser().parseFromString(html_str, "text/hxml");
                
                // insert it to table
                var tbody = document.getElementsByTagName("tbody");
                tbody.appendChild(new_row);
                
            } else if (xhr.status == 400 && jresp.ok == false) {
                alert(jresp.error);
            }
        }
    }
    
    xhr.send(data);
}

function toggleVisited(id)
{
    // update visit status
    var xhr = new XMLHttpRequest();
    xhr.open("PUT", `/api/sharetitle/${id}`, true);
    xhr.send();
    
    // update element
    xhr.onreadystatechange = function(responseText) {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            
            var jresp = JSON.parse(responseText.target.response);
            var vs_th = document.querySelector(`[id="${jresp.id}"] .visitStatus`);
            var time_th = document.querySelector(`[id="${jresp.id}"] .time`);
            var glb_vs_th = globalThis.websiteTableDOM.querySelector(`[id="${id}"] .visitStatus`);
            var glb_time_th = globalThis.websiteTableDOM.querySelector(`[id="${id}"] .time`);
            var filter_mode = parseInt(sessionStorage.getItem("filter_mode"));
            
            // success
            if (xhr.status == 200 && jresp.ok == true) {
                // update row element 
                time_th.setAttribute("time", jresp.time);
                time_th.innerHTML = jresp.time;
                glb_time_th.setAttribute("time", jresp.time);
                glb_time_th.innerHTML = jresp.time;
                if (jresp.is_visited == 0) {
                    vs_th.children[0].innerHTML = "unvisited";
                    vs_th.children[0].style.color = "red";
                    vs_th.setAttribute("visited", 0);
                    glb_vs_th.children[0].innerHTML = "unvisited";
                    glb_vs_th.children[0].style.color = "red";
                    glb_vs_th.setAttribute("visited", 0);
                } else if (jresp.is_visited == 1) {
                    vs_th.children[0].innerHTML = "visited";
                    vs_th.children[0].style.color = "green";
                    vs_th.setAttribute("visited", 1);
                    glb_vs_th.children[0].innerHTML = "visited";
                    glb_vs_th.children[0].style.color = "green";
                    glb_vs_th.setAttribute("visited", 1);
                }
                
                beautifyRow(jresp.id);
                
                // rm current row in table & update sharetitle count
                if (filter_mode != -1 && filter_mode != jresp.is_visited) {
                    vs_th.parentNode.parentNode.removeChild(vs_th.parentNode);
                    globalThis.filteredWebsiteCount--;
                    var table_title = document.getElementById("table_title");
                    table_title.innerHTML = table_title.innerHTML.replace(/#|\d+/, globalThis.filteredWebsiteCount.toString());
                }
                
            }
            // fail
            else if (xhr.status == 400 && jresp.ok == false) {
                alert(jresp.error);
            }
        }
    }
    
}

function filterWebsites()
{
    var tbody = document.querySelector("tbody");
    var mode = parseInt(document.getElementById("fileter").value);
    var counter = 0;
    
    // rm all children from DOM
    while (tbody.children.length > 1) {
        tbody.removeChild(tbody.children[1]);
    }
    
    // filter & append websites to tbody
    if (mode == -1) {
        globalThis.websiteTableDOM.querySelectorAll("tr[id]").forEach(function(item){
            tbody.appendChild(item.cloneNode(true));
            beautifyRow(item.getAttribute("id"));
            counter++;
        })
    } else {
        Array.from(globalThis.websiteTableDOM.querySelectorAll("tr[id]")).filter(
            function(a){
                return a.innerHTML.includes(`visited="${mode}"`)
            }
        ).forEach(function(item){
            tbody.appendChild(item.cloneNode(true));
            beautifyRow(item.getAttribute("id"));
            counter++;
        })
    }
    
    // update sharetitle count on table_title
    globalThis.filteredWebsiteCount = counter;
    var table_title = document.getElementById("table_title");
    table_title.innerHTML = table_title.innerHTML.replace(/#|\d+/, globalThis.filteredWebsiteCount.toString());
    
    // remember user's filter choice in current session
    sessionStorage.setItem("filter_mode", mode);
}

function autoFilterWebsites()
{
    // auto run filterWebsites() & use user's filter choice
    var mode = -1;
    var sess = sessionStorage.getItem("filter_mode");
    if (sess == null)
        mode = 0;
    else
        mode = sess;
    
    document.getElementById("fileter").value = mode;
    filterWebsites();
}

function beautifyRow(id)
{
    var tr_elem = document.getElementById(id);
    
    // beautify link
    var link_th = tr_elem.querySelector(".link");
    var link_domain = link_th.getAttribute("domain");
    var link_pc = parseInt(link_th.getAttribute("parentChild"));
    if (link_pc == 0) link_pc = "parent";
    else if (link_pc == 1) link_pc = "child";
    link_th.children[0].innerHTML = `${link_domain} ${link_pc}`;
    
    // beautify visitStatus
    var vs_th = tr_elem.querySelector(".visitStatus");
    var visited = parseInt(vs_th.getAttribute("visited"));
    if (visited == 0)
    {
        vs_th.children[0].innerHTML = "unvisited";
        vs_th.children[0].style.color = "red";
    }
    else if (visited == 1)
    {
        vs_th.children[0].innerHTML = "visited";
        vs_th.children[0].style.color = "green";
    }
    
    // beautify time
    var time_th = tr_elem.querySelector(".time");
    var time = new Date(parseInt(time_th.getAttribute("time"))*1000);
    var y = String(time.getFullYear());
    var m = String(time.getMonth()+1).padStart(2, '0');
    var d = String(time.getDate()).padStart(2, '0');
    var H = String(time.getHours()).padStart(2, '0');
    var M = String(time.getMinutes()).padStart(2, '0');
    var S = String(time.getSeconds()).padStart(2, '0');
    
    time_th.innerHTML = `${y}-${m}-${d} ${H}:${M}:${S}`;
}

function deleteSharetitle(id)
{
    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", `/api/sharetitle/${id}`, true);
    
    // update page after response
    xhr.onreadystatechange = function(responseText) {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            var jresp = JSON.parse(responseText.target.response);
            if (xhr.status == 200 && jresp.ok == true) {
                
                var vs_th = document.querySelector(`[id="${jresp.id}"] .visitStatus`);
                var glb_vs_th = globalThis.websiteTableDOM.querySelector(`[id="${jresp.id}"] .visitStatus`);
                
                // rm current row in table & update sharetitle count
                vs_th.parentNode.parentNode.removeChild(vs_th.parentNode);
                glb_vs_th.parentNode.parentNode.removeChild(glb_vs_th.parentNode);
                globalThis.filteredWebsiteCount--;
                var table_title = document.getElementById("table_title");
                table_title.innerHTML = table_title.innerHTML.replace(/#|\d+/, globalThis.filteredWebsiteCount.toString());
                
            } else if (xhr.status == 400 && jresp.ok == false) {
                alert(jresp.error);
            }
        }
    }
    
    xhr.send();
}

function sortTable(sortMode, keyFn)
{
    var all_tr = document.querySelectorAll("tbody tr[id]");
    // sort ascending
    if (sortMode == 1)
    {
        all_tr = Array.from(all_tr).sort(function(a, b){
            var a = keyFn(a);
            var b = keyFn(b);
            if (a > b) return 1;
            else if (a < b) return -1;
            else return 0;
        });
    }
    // sort descending
    else if (sortMode == 2)
    {
        all_tr = Array.from(all_tr).sort(function(a, b){
            var a = keyFn(a);
            var b = keyFn(b);
            if (a > b) return -1;
            else if (a < b) return 1;
            else return 0;
        });
    }
    
    // rm all children from DOM
    var tbody = document.querySelector("tbody");
    while (tbody.children.length > 1) {
        tbody.removeChild(tbody.children[1]);
    }
    
    // put sorted list into DOM
    all_tr.forEach(function(row){
        tbody.appendChild(row.cloneNode(true));
        beautifyRow(row.getAttribute("id"));
    });
}

function sortById()
{
    if (globalThis.sortByIdStat == 0)
    {
        globalThis.sortByTimeStat = 0;
        globalThis.sortByIdStat = 1;
        sortTable(globalThis.sortByIdStat, function(row){
            return parseInt(row.getAttribute("id"));
        });
        document.querySelector("tbody").children[0].children[4].innerHTML = "Time&nbsp;&nbsp;";
        document.querySelector("tbody").children[0].children[0].innerHTML = "Id&nbsp;???";
    }
    else if (globalThis.sortByIdStat == 1)
    {
        globalThis.sortByIdStat = 2;
        sortTable(globalThis.sortByIdStat, function(row){
            return parseInt(row.getAttribute("id"));
        });
        document.querySelector("tbody").children[0].children[0].innerHTML = "Id&nbsp;???";
    }
    else if (globalThis.sortByIdStat == 2)
    {
        globalThis.sortByIdStat = 1;
        sortTable(globalThis.sortByIdStat, function(row){
            return parseInt(row.getAttribute("id"));
        });
        document.querySelector("tbody").children[0].children[0].innerHTML = "Id&nbsp;???";
    }
}
function sortByTime()
{
    if (globalThis.sortByTimeStat == 0)
    {
        globalThis.sortByIdStat = 0;
        globalThis.sortByTimeStat = 1;
        sortTable(globalThis.sortByTimeStat, function(row){
            return parseInt(row.querySelector(".time").getAttribute("time"));
        });
        document.querySelector("tbody").children[0].children[0].innerHTML = "Id&nbsp;&nbsp;";
        document.querySelector("tbody").children[0].children[4].innerHTML = "Time&nbsp;???";
    }
    else if (globalThis.sortByTimeStat == 1)
    {
        globalThis.sortByTimeStat = 2;
        sortTable(globalThis.sortByTimeStat, function(row){
            return parseInt(row.querySelector(".time").getAttribute("time"));
        });
        document.querySelector("tbody").children[0].children[4].innerHTML = "Time&nbsp;???";
    }
    else if (globalThis.sortByTimeStat == 2)
    {
        globalThis.sortByTimeStat = 1;
        sortTable(globalThis.sortByTimeStat, function(row){
            return parseInt(row.querySelector(".time").getAttribute("time"));
        });
        document.querySelector("tbody").children[0].children[4].innerHTML = "Time&nbsp;???";
    }
}
