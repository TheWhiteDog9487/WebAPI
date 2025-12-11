package xyz.thewhitedog9487.WebAPI.Configuration;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(RegisterGraalVMBuildHints.class)
class GraalVMBuildHints {}

/**
 * GPT写的，测试了一下能用，先作为实验性内容加入
 * @author ChatGPT GPT5
 */
class RegisterGraalVMBuildHints implements RuntimeHintsRegistrar{

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        String basePackage = "discord4j";
        String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        CachingMetadataReaderFactory mrf = new CachingMetadataReaderFactory(classLoader);

        int ok = 0, skip = 0;
        try {
            Resource[] resources = resolver.getResources(pattern);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                MetadataReader mr = mrf.getMetadataReader(resource);
                String className = mr.getClassMetadata().getClassName();
                try {
                    // 只处理目标包（防御性判断，避免扫到别的）
                    if (!className.startsWith(basePackage)) {
                        continue;
                    }
                    Class<?> clazz = ClassUtils.forName(className, classLoader);
                    // 暴力但稳：把构造器/方法/字段都暴露给反射（含内部类、Builder、$Json）
                    hints.reflection().registerType(clazz,b -> b.withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.ACCESS_DECLARED_FIELDS));
                    ok++;
                } catch (Throwable e) {
                    // 有些类可能因缺失依赖/被 JDK 模块限制而加载失败，跳过即可
                    skip++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Scan & register discord4j classes failed", e);
        }

        hints.reflection().registerType(
            TypeReference.of("com.github.benmanes.caffeine.cache.SSLMS"),
            builder -> builder.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.ACCESS_DECLARED_FIELDS
            )
        );

        hints.reflection().registerType(
            TypeReference.of("com.github.benmanes.caffeine.cache.SSMS"),
            builder -> builder.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.ACCESS_DECLARED_FIELDS
            )
        );

        hints.reflection().registerType(
            TypeReference.of("com.github.benmanes.caffeine.cache.PSLMS"),
            builder -> builder.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.ACCESS_DECLARED_FIELDS
            )
        );

        hints.reflection().registerType(
            TypeReference.of("com.github.benmanes.caffeine.cache.PSMS"),
            builder -> builder.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.ACCESS_DECLARED_FIELDS
            )
        );


        // 用 System.out 打印（AOT 阶段 SLF4J 常常是 NOP）
        System.out.println("[Discord4jJsonHints] registered classes: " + ok + ", skipped: " + skip);

    }
    
}
