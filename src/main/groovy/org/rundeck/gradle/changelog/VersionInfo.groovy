package org.rundeck.gradle.changelog

import com.github.zafarkhaja.semver.Version
import pl.allegro.tech.build.axion.release.domain.VersionContext

class VersionInfo {
    VersionContext version
    String nextTag
    String lastTag
}
