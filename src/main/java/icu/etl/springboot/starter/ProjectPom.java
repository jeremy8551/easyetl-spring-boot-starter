package icu.etl.springboot.starter;

/**
 * 工程配置
 */
public class ProjectPom {

    /**
     * 返回构件的 groupId
     *
     * @return groupId
     */
    public static String getGroupID() {
        return "icu.etl";
    }

    /**
     * 返回构件的 artifactId
     *
     * @return artifactId
     */
    public static String getArtifactID() {
        return "easyetl-spring-boot-starter";
    }

    /**
     * 返回构件的 version
     *
     * @return version
     */
    public static String getVersion() {
        return "2.0.9";
    }

}
