echo "starting removing logs"
for filename in $(find /var/log -type f -mtime +30); do
        echo "removing $filename"
        rm $filename -f
done
echo "log cleanup done"