# Pinary's Library

This library currently only adds some minor improvements to block model generation. I plan on adding a lot more functionality in the future.

## Download and Other Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/pinarys-library) (download link)
- [GitHub Repository](https://github.com/Pinary-Pi/Pinarys-Library) (source code)
- [GitHub Issue Tracker](https://github.com/Pinary-Pi/Pinarys-Library/issues) (issue tracker)

## Developers - How to Use
You need to edit your `build.gradle`, but before you do that you need to add a [GitHub token](https://github.com/settings/tokens) to a personal `gradle.properties`. I have my `gradle.properties` in `C:\Users\MY_USER\.gradle` and I recommend putting yours there too. 

`gradle.properties` example:

```gradle.properties
# Username
gpr.username=Your-Username

# Token
gpr.token=your_personal_access_token
``` 
 You then need to add this to the `build.gradle`
```gradle
def gpr_creds = {
    username = property('gpr.user')
    password = property('gpr.tokenread')
}

repositories {
    maven {
        setUrl("https://maven.pkg.github.com/pinary-pi/pinaryslib")
        credentials gpr_creds
    }
}
```
And in the dependencies
```gradle
compile fg.deobf('net.Pinary_Pi.pinaryslib:pinarys-library:1.0.1') {
        exclude module: "forge"
    }
```
If this doesn't work, post an issue in the issue tracker and I can try to help you.
