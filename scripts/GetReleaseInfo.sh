sha1() {
  sha1sum $1 | awk '{print $1}'
}

md5() {
  md5sum $1 | awk '{print $1}'
}

prop() {
  grep "${1}" gradle.properties | cut -d'=' -f2 | sed 's/\r//'
}

commitid=$(git log --pretty='%h' -1)
mcversion=$(prop mcVersion)
gradleVersion=$(prop version)
preVersion=$(prop preVersion)
tagid="$mcversion-$commitid"
jarName="etheria-$mcversion.jar"
etheriaid="Etheria-$commitid"
releaseinfo="releaseinfo.md"
discordmes="discordmes.json"
make_latest=$([ $preVersion = "true" ] && echo "false" || echo "true")

rm -f $discordmes
rm -f $releaseinfo

mv build/libs/etheria-paperclip-$gradleVersion-reobf.jar $jarName
echo "name=$etheriaid" >> $GITHUB_ENV
echo "tag=$tagid" >> $GITHUB_ENV
echo "jar=$jarName" >> $GITHUB_ENV
echo "info=$releaseinfo" >> $GITHUB_ENV
echo "discordmes=$discordmes" >> $GITHUB_ENV
echo "pre=$preVersion" >> $GITHUB_ENV
echo "make_latest=$make_latest" >> $GITHUB_ENV

echo "[![download](https://img.shields.io/github/downloads/sub-kek/Etheria/$tagid/total?color=0)](https://github.com/EtheriaMC/Etheria/releases/download/$tagid/$jarName)" >> $releaseinfo
echo "=====" >> $releaseinfo
echo "" >> $releaseinfo
if [ $preVersion = "true" ]; then
  echo "> This is an early, experimental build. It is only recommended for usage on test servers and should be used with caution." >> $releaseinfo
  echo "> **Backups are mandatory!**" >> $releaseinfo
  echo "" >> $releaseinfo
fi
echo "### Commit Message" >> $releaseinfo

number=$(git log --oneline master `^git describe --tags --abbrev=0` | wc -l)
echo "$(git log --pretty='> [%h] %s' -$number)" >> $releaseinfo

echo "" >> $releaseinfo
echo "### Checksum" >> $releaseinfo
echo "| File | $jarName |" >> $releaseinfo
echo "| ---- | ---- |" >> $releaseinfo
echo "| MD5 | `md5 $jarName` |" >> $releaseinfo
echo "| SHA1 | `sha1 $jarName` |" >> $releaseinfo

echo -n "{\"avatar_url\":\"https://ely.by/services/skins-renderer?url=http://skinsystem.ely.by/skins/subkek.png&scale=18&renderFace=1\",\"content\":\"Etheria New Release\",\"embeds\":[{\"color\":16755172,\"title\":\"$etheriaid\",\"url\":\"https://github.com/sub-kek/Etheria/releases/tag/$tagid\",\"fields\":[{\"name\":\"Changelog\",\"value\":\"" >> $discordmes
echo -n $(git log --oneline --pretty='> [%h] %s\\n' -$number | sed "s/\"/\\\\\"/g") >> $discordmes
echo "\",\"inline\":true}]}]}" >> $discordmes
