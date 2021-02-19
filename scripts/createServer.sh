serverGroup=$1
serverId=$2
serverJarFile=$3
templateName=$4
pluginName=$5
worldPath=$6
port=$7
startupScript=$8

mainDirectory=/home/minecraft
groupPath=$mainDirectory/servers/$serverGroup
serverPath=$groupPath/$serverId

# Create the group directory if the directory doesn't exist
mkdir -p "$groupPath"

# If the server directory already exists, delete it
if [ -d "$serverPath" ]; then
	rm -r -f "$serverPath"
fi

# Create the server directory
mkdir "$serverPath"
cd "$serverPath" || exit

# Copying the server jar file to the server
cp $mainDirectory/upload/jars/"$serverJarFile" "$serverPath"/server.jar

# Copying the server template to the server, unzipping it, and then removing the zip
cp $mainDirectory/upload/templates/"$templateName" template.zip
unzip template.zip
rm -r template.zip

# Copying the plugin into the server's plugin directory
cp $mainDirectory/upload/jars/"$pluginName" plugins/"$pluginName"

# Copying the world
mkdir world
cp $mainDirectory/upload/maps/"$worldPath" world/world.zip
cd world/ || exit
unzip world.zip
rm -r world.zip
cd ..

# Accepting the eula
touch eula.txt
echo "eula=true" >> eula.txt

# Writing the port to the server.properties file
touch server.properties
echo "server-ip=0.0.0.0" >> server.properties
echo "server-port=$port" >> server.properties

# Starting the server
screen -dmS minecraftServer-"$serverId"
screen -S minecraftServer-"$serverId" -X exec "$startupScript"