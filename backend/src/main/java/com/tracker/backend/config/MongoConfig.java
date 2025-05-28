package com.tracker.backend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    /* * Configuración de la conexión a MongoDB
     * Asegurarse de que la URI de conexión sea correcta y que el usuario tenga los permisos necesarios.
     */
    @Bean
    public MongoClient mongoClient() {
        String uri = "mongodb+srv://lacruztristan:contrasena123@clusterappcrypto.r9skw.mongodb.net/appDB?retryWrites=true&w=majority";
        return MongoClients.create(uri);
    }

    /**
     * Configuración del MongoTemplate
     * Se utiliza para realizar operaciones CRUD en la base de datos.
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "appDB");
    }
}
