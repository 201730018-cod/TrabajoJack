const express = require('express');
const cors = require('cors');
const {MongoClient} = require('mongodb')

const uri = "mongodb://jackasii:ventiladO1@ac-yktfvsa-shard-00-00.rrzfdeq.mongodb.net:27017,ac-yktfvsa-shard-00-01.rrzfdeq.mongodb.net:27017,ac-yktfvsa-shard-00-02.rrzfdeq.mongodb.net:27017/?ssl=true&replicaSet=atlas-8iwyhf-shard-0&authSource=admin&appName=Jack1"

const client = new MongoClient(uri);

async function conectarMongoDB(){
    try{
        await client.connect();
        console.log("Conectado a MongoDB");
                return client.db("sample_mflix"); 

        
    } catch(error){
        console.error("Error en la conexion a MongoDB")
        process.exit(1);
    }
}

const app = express();
const port = 4000;

app.use(express.json());
app.use(cors());

let db;

conectarMongoDB().then(
    database => {
        db = database;
        console.log("Base de datos lista...");
    }
);

app.get("/movies", async (req,res)=>{
    try{
        const movies = await db.collection("movies").find(
            {},{projection:{poster:1, title:1, fullplot:1}}
        ).limit(50).toArray();
        res.json(movies);
    } catch(error){
        res.status(500).json({mensaje:"Error al obtener los datos de la coleccion"});
    }
});

app.listen(port, ()=>{
    console.log("Servidor en http://localhost:4000")
});