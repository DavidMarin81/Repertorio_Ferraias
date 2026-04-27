using System.ComponentModel.DataAnnotations;

namespace RepertorioFerraias.Models

{
    public class Pieza
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "El titulo es obligatorio.")]
        public string Titulo { get; set; } = string.Empty;
        [Required(ErrorMessage = "El estilo es obligatorio.")]
        public Estilo Estilo { get; set; }
        [Range(1, 5)]
        public int Puntuacion { get; set; }
    }

    public enum Estilo
    {
        Muineira,
        Xota,
        Pasodobre,
        Rumba,
        Pasacorredoira,
        Mazurca,
        Outros
    }
}
