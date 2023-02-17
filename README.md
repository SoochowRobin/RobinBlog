#Robin Blog ##

This repo contains the source code for building a personal blog website. The main technique stack is Java Spring as backend
Vue framework as frontend. What I have harvested from this project are listed as followed:<br>

<ul>
<li>Use Spring Security to make authentication and authorization</li>
<li>Use Redis to store token returned from backend to help authentication</li>
<li>Use OSS(Object Storage Service) to store images in cloud, lifting pressure on web server</li>
<li>Use CommandLineRunner to get data in Redis before application running</li>
<li>Use CORN formulate to set a timer work to update value in Redis to Database</li>
<li>Use Swagger2 to document API design and return type</li>
<li>Use Spring AOP to record log by using @SLF4j</li>

</ul>