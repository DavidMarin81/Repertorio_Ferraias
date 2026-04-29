using System.ComponentModel.DataAnnotations;

namespace RepertorioFerraias.Models
{
    public class CreateOrUpdatePiezaRequest
    {
        [Required(ErrorMessage = "El titulo es obligatorio.")]
        public string Titulo { get; set; } = string.Empty;

        [Required(ErrorMessage = "El estilo es obligatorio.")]
        public Estilo Estilo { get; set; }

        [Range(1, 5)]
        public int Puntuacion { get; set; }
    }
}
