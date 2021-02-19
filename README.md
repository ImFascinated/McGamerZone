# Project Structure
- **Commons**: Commons is common libraries and/or utilities that are shared across the network.
- **ServerData**: This branch of the project controls the database backend for both Redis and MySQL. All modules that use Redis or MySQL must have this as a dependency.
- **ServerController**: This will dynamically start and stop servers on demand.
- **Proxy**: The proxy will handle server balancing and player caching.
- **API**: This is the frontend of the project if you will. All developers will be given access to this branch of the project where they can access multiple parts of the server.
- **Core**: The core is a shared module between all Spigot plugins. Everything used between multiple Spigot servers will be created here.
- **Build Server**: The core for the build server - This handles map creating, parsing, etc
- **Hub**: This is pretty self-explanatory. Any Hub related things will go here.
- **Arcade**: This is pretty self-explanatory. This is the game core and all Arcade related things will go here.
- **Skyblock**: As we use a public Skyblock plugin, any custom things related to Skyblock will go here.
- **DiscordBot**: This is pretty self-explanatory. Any Discord related things will go here.
- **Testing**: This part of the project is strictly for testing purposes.

# Redis
Redis is used for a variety of different things. Mainly, Redis is used for the server backend and as a global cache.

# MySQL
MySQL is used for the main account backend. Everything related to accounts will be stored here.

# Production
When something is being released for production, you must set the production parameter in the **MySQLController** class and make sure you are on the **master** branch!

# Before Starting
- Create a **gradle.properties** file which will contain your
Nexus username and password. (It's recommended to add the properties below to your **gradle.properties** file to allow the use of parallel compiling and caching)
- Stick to Java naming conventions, which can be found [here](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)
```properties
org.gradle.parallel=true
org.gradle.caching=true
gradle.cache.push=false
```

# Building From Source
- Clone the project
- Open the project in your IDE of choice (Intellij is highly recommended)
- Run the task **shadowJar** which can be found under **McGamerCore** » **Tasks** » **shadow**

**If you are confused on how to do things, or you're unsure on where to put something, please contact Braydon on Discord**