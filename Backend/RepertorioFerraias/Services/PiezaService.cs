using Microsoft.Extensions.Options;
using MongoDB.Driver;
using RepertorioFerraias.Models;
using RepertorioFerraias.Settings;

namespace RepertorioFerraias.Services
{
    public class PiezaService
    {
        private readonly IMongoCollection<Pieza> _piezas;

        public PiezaService(IOptions<MongoDbSettings> settings)
        {
            var mongoClient = new MongoClient(settings.Value.ConnectionString);

            var database = mongoClient.GetDatabase(settings.Value.DatabaseName);

            _piezas = database.GetCollection<Pieza>(settings.Value.CollectionName);
        }

        public async Task<List<Pieza>> GetAsync() =>
            await _piezas.Find(_ => true).ToListAsync();

        public async Task CreateAsync(Pieza pieza) =>
            await _piezas.InsertOneAsync(pieza);
    }
}