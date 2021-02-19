serverGroup=$1
serverId=$2

logsDirectory=/home/minecraft/serverLogs/$serverGroup

# Create the group logs directory if the directory doesn't exist
mkdir -p "$logsDirectory"

cp /home/minecraft/servers/$serverGroup/$serverId/logs/latest.log "$logsDirectory"/"$serverId-$(date +"%Y_%m_%d_%I_%M_%p").log"

screen -S minecraftServer-"$serverId" -X kill
screen -S minecraftServer-"$serverId" -X quit

rm -r "$serverDirectory"