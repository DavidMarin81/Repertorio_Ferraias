using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using RepertorioFerraias.Models;
using RepertorioFerraias.Settings;

namespace RepertorioFerraias.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PiezasController : ControllerBase
    {
        private readonly IMongoCollection<Pieza> _piezas;
        private readonly ILogger<PiezasController> _logger;

        public PiezasController(IOptions<MongoDbSettings> settings, ILogger<PiezasController> logger)
        {
            _logger = logger;
            var mongoClient = new MongoClient(settings.Value.ConnectionString);
            var database = mongoClient.GetDatabase(settings.Value.DatabaseName);
            _piezas = database.GetCollection<Pieza>(settings.Value.CollectionName);
        }

        // POST: api/piezas
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] Pieza pieza)
        {
            try
            {
                await _piezas.InsertOneAsync(pieza);
                return CreatedAtAction(nameof(GetById), new { id = pieza.Id }, pieza);
            }
            catch (MongoException)
            {
                return Problem(
                    detail: "No se pudo guardar la pieza en la base de datos.",
                    statusCode: 500,
                    title: "Error al crear la pieza");
            }
        }

        // GET: api/piezas
        [HttpGet]
        public async Task<ActionResult<List<Pieza>>> GetAll()
        {
            try
            {
                var piezas = await _piezas.Find(_ => true).ToListAsync();
                return Ok(piezas);
            }
            catch (MongoException)
            {
                return Problem(
                    detail: "No se pudieron recuperar las piezas desde la base de datos.",
                    statusCode: 500,
                    title: "Error al obtener las piezas");
            }
        }

        // GET: api/piezas/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Pieza>> GetById(int id)
        {
            try
            {
                var pieza = await _piezas.Find(p => p.Id == id).FirstOrDefaultAsync();

                if (pieza is null)
                {
                    return NotFound(new { mensaje = $"No existe ninguna pieza con id {id}." });
                }

                return Ok(pieza);
            }
            catch (MongoException)
            {
                return Problem(
                    detail: "No se pudo recuperar la pieza desde la base de datos.",
                    statusCode: 500,
                    title: "Error al obtener la pieza");
            }
        }

        // PUT: api/piezas/5
        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] Pieza piezaActualizada)
        {
            if (id != piezaActualizada.Id)
            {
                return BadRequest(new { mensaje = "El id de la URL no coincide con el id de la pieza." });
            }

            try
            {
                var resultado = await _piezas.ReplaceOneAsync(p => p.Id == id, piezaActualizada);

                if (resultado.MatchedCount == 0)
                {
                    return NotFound(new { mensaje = $"No existe ninguna pieza con id {id}." });
                }

                return Ok(new { mensaje = "Pieza actualizada correctamente.", pieza = piezaActualizada });
            }
            catch (MongoException)
            {
                return Problem(
                    detail: "No se pudo actualizar la pieza en la base de datos.",
                    statusCode: 500,
                    title: "Error al actualizar la pieza");
            }
        }

        // DELETE: api/piezas/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                var resultado = await _piezas.DeleteOneAsync(p => p.Id == id);

                if (resultado.DeletedCount == 0)
                {
                    return NotFound(new { mensaje = $"No existe ninguna pieza con id {id}." });
                }

                return Ok(new { mensaje = "Pieza eliminada correctamente." });
            }
            catch (MongoException)
            {
                return Problem(
                    detail: "No se pudo eliminar la pieza de la base de datos.",
                    statusCode: 500,
                    title: "Error al eliminar la pieza");
            }
        }
    }
}
