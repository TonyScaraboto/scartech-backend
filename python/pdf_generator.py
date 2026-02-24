# Script Python para gerar PDF de ordem de serviço
import sys
import json
import os
from datetime import datetime

try:
    from fpdf import FPDF
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "fpdf2"])
    from fpdf import FPDF

# ===== PALETA DE CORES - UI/UX OTIMIZADO =====
class Cores:
    PRIMARIO = (31, 41, 55)          # Cinza-900: #1F2937 - Headers e títulos
    SECUNDARIO = (37, 99, 235)       # Azul-600: #2563EB - Destaques
    TERTIARY = (16, 185, 129)        # Esmeralda: #10B981 - Status positivo/valores
    TEXTO_PRINCIPAL = (17, 24, 39)   # Cinza-900: #111827 - Texto principal
    TEXTO_SECUNDARIO = (107, 114, 128) # Cinza-500: #6B7280 - Labels
    FUNDO_CLARO = (243, 244, 246)    # Cinza-100: #F3F4F6 - Fundos de seções
    FUNDO_AZUL = (239, 246, 255)     # Azul-50: #EFF6FF - Fundo mais soft
    BORDAS = (191, 219, 254)         # Azul-200: #BFDBFE - Bordas e linhas
    NEGACAO = (239, 68, 68)          # Vermelho: #EF4444 - Para avisos

class OrdemServicoPDF(FPDF):
    def header(self):
        self.set_font('Helvetica', 'B', 18)
        self.set_text_color(*Cores.PRIMARIO)
        self.cell(0, 12, 'ORDEM DE SERVIÇO', border=0, ln=True, align='C')
        self.set_font('Helvetica', '', 10)
        self.set_text_color(*Cores.TEXTO_SECUNDARIO)
        self.cell(0, 6, 'Assistência Técnica ScarTech', border=0, ln=True, align='C')
        self.ln(8)
        self.set_draw_color(*Cores.BORDAS)
        self.line(10, self.get_y(), 200, self.get_y())
        self.ln(5)

    def footer(self):
        self.set_y(-20)
        self.set_font('Helvetica', 'I', 8)
        self.set_text_color(*Cores.TEXTO_SECUNDARIO)
        self.cell(0, 10, f'Página {self.page_no()}', align='C')

def generate_pdf(order_data):
    pdf = OrdemServicoPDF()
    pdf.add_page()
    pdf.set_auto_page_break(auto=True, margin=25)

    # Número da OS e Data
    pdf.set_font('Helvetica', 'B', 11)
    pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
    data_atual = datetime.now().strftime('%d/%m/%Y %H:%M')
    pdf.cell(95, 8, f'Data: {data_atual}', border=0, ln=False)
    pdf.cell(95, 8, f'OS Nº: {datetime.now().strftime("%Y%m%d%H%M%S")}', border=0, ln=True, align='R')
    pdf.ln(5)

    # Dados do Cliente
    pdf.set_fill_color(*Cores.FUNDO_AZUL)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(*Cores.PRIMARIO)
    pdf.cell(0, 10, 'DADOS DO CLIENTE', border=0, ln=True, fill=True)
    pdf.ln(2)

    pdf.set_font('Helvetica', '', 11)
    pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
    
    def add_field(label, value):
        pdf.set_font('Helvetica', 'B', 10)
        pdf.set_text_color(*Cores.TEXTO_SECUNDARIO)
        pdf.cell(50, 8, label + ':', border=0)
        pdf.set_font('Helvetica', '', 10)
        pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
        pdf.cell(0, 8, str(value or '-'), border=0, ln=True)

    add_field('Nome', order_data.get('nomeCliente', ''))
    add_field('Documento', order_data.get('documentoCliente', ''))
    add_field('Telefone', order_data.get('telefoneCliente', ''))
    pdf.ln(5)

    # Dados do Aparelho
    pdf.set_fill_color(*Cores.FUNDO_AZUL)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(*Cores.PRIMARIO)
    pdf.cell(0, 10, 'DADOS DO APARELHO', border=0, ln=True, fill=True)
    pdf.ln(2)

    pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
    add_field('Modelo', order_data.get('modeloAparelho', ''))
    
    pdf.set_font('Helvetica', 'B', 10)
    pdf.set_text_color(*Cores.TEXTO_SECUNDARIO)
    pdf.cell(50, 8, 'Defeito:', border=0)
    pdf.set_font('Helvetica', '', 10)
    pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
    pdf.multi_cell(0, 8, str(order_data.get('defeitoApresentado', '-')))
    pdf.ln(5)

    # Valor do Conserto - DESTAQUE COM COR VERDE ESMERALDA
    pdf.set_fill_color(*Cores.TERTIARY)
    pdf.set_font('Helvetica', 'B', 13)
    pdf.set_text_color(255, 255, 255)  # Branco para contraste
    valor = order_data.get('valorConserto', '0')
    pdf.cell(0, 14, f'VALOR DO CONSERTO: R$ {valor}', border=0, ln=True, fill=True, align='C')
    pdf.ln(5)

    # Termo de Garantia
    pdf.set_fill_color(*Cores.FUNDO_AZUL)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(*Cores.PRIMARIO)
    pdf.cell(0, 10, 'TERMO DE GARANTIA', border=0, ln=True, fill=True)
    pdf.ln(2)

    pdf.set_font('Helvetica', '', 10)
    pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
    pdf.multi_cell(0, 7, str(order_data.get('termoGarantia', 'Garantia de 90 dias conforme legislação vigente.')))
    pdf.ln(10)

    # Assinaturas
    pdf.set_font('Helvetica', 'B', 11)
    pdf.set_text_color(*Cores.PRIMARIO)
    pdf.cell(0, 8, 'ASSINATURAS', border=0, ln=True)
    pdf.ln(15)

    pdf.set_draw_color(*Cores.BORDAS)
    pdf.set_text_color(*Cores.TEXTO_PRINCIPAL)
    pdf.set_font('Helvetica', '', 10)
    
    # Linha assinatura técnico
    pdf.line(15, pdf.get_y(), 90, pdf.get_y())
    pdf.line(115, pdf.get_y(), 195, pdf.get_y())
    pdf.ln(3)
    pdf.cell(95, 6, 'Assinatura do Técnico', border=0, align='C')
    pdf.cell(95, 6, 'Assinatura do Cliente', border=0, align='C', ln=True)

    # Salvar PDF
    output_dir = os.path.join(os.path.dirname(__file__), 'pdfs')
    os.makedirs(output_dir, exist_ok=True)
    
    filename = f'OS_{datetime.now().strftime("%Y%m%d_%H%M%S")}.pdf'
    filepath = os.path.join(output_dir, filename)
    pdf.output(filepath)
    
    return f"PDF gerado com sucesso: {filename}"

if __name__ == "__main__":
    if len(sys.argv) > 1:
        order_data = json.loads(sys.argv[1])
        print(generate_pdf(order_data))
    else:
        print("Erro: dados da ordem não fornecidos")
