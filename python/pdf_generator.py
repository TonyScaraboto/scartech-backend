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

class OrdemServicoPDF(FPDF):
    def header(self):
        self.set_font('Helvetica', 'B', 18)
        self.set_text_color(37, 99, 235)
        self.cell(0, 12, 'ORDEM DE SERVIÇO', border=0, ln=True, align='C')
        self.set_font('Helvetica', '', 10)
        self.set_text_color(100, 100, 100)
        self.cell(0, 6, 'Assistência Técnica ScarTech', border=0, ln=True, align='C')
        self.ln(8)
        self.set_draw_color(37, 99, 235)
        self.line(10, self.get_y(), 200, self.get_y())
        self.ln(5)

    def footer(self):
        self.set_y(-20)
        self.set_font('Helvetica', 'I', 8)
        self.set_text_color(128, 128, 128)
        self.cell(0, 10, f'Página {self.page_no()}', align='C')

def generate_pdf(order_data):
    pdf = OrdemServicoPDF()
    pdf.add_page()
    pdf.set_auto_page_break(auto=True, margin=25)

    # Número da OS e Data
    pdf.set_font('Helvetica', 'B', 11)
    pdf.set_text_color(0, 0, 0)
    data_atual = datetime.now().strftime('%d/%m/%Y %H:%M')
    pdf.cell(95, 8, f'Data: {data_atual}', border=0, ln=False)
    pdf.cell(95, 8, f'OS Nº: {datetime.now().strftime("%Y%m%d%H%M%S")}', border=0, ln=True, align='R')
    pdf.ln(5)

    # Dados do Cliente
    pdf.set_fill_color(240, 245, 255)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(37, 99, 235)
    pdf.cell(0, 10, 'DADOS DO CLIENTE', border=0, ln=True, fill=True)
    pdf.ln(2)

    pdf.set_font('Helvetica', '', 11)
    pdf.set_text_color(0, 0, 0)
    
    def add_field(label, value):
        pdf.set_font('Helvetica', 'B', 10)
        pdf.cell(50, 8, label + ':', border=0)
        pdf.set_font('Helvetica', '', 10)
        pdf.cell(0, 8, str(value or '-'), border=0, ln=True)

    add_field('Nome', order_data.get('nomeCliente', ''))
    add_field('Documento', order_data.get('documentoCliente', ''))
    add_field('Telefone', order_data.get('telefoneCliente', ''))
    pdf.ln(5)

    # Dados do Aparelho
    pdf.set_fill_color(240, 245, 255)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(37, 99, 235)
    pdf.cell(0, 10, 'DADOS DO APARELHO', border=0, ln=True, fill=True)
    pdf.ln(2)

    pdf.set_text_color(0, 0, 0)
    add_field('Modelo', order_data.get('modeloAparelho', ''))
    
    pdf.set_font('Helvetica', 'B', 10)
    pdf.cell(50, 8, 'Defeito:', border=0)
    pdf.set_font('Helvetica', '', 10)
    pdf.multi_cell(0, 8, str(order_data.get('defeitoApresentado', '-')))
    pdf.ln(5)

    # Valor do Conserto
    pdf.set_fill_color(220, 252, 231)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(34, 197, 94)
    valor = order_data.get('valorConserto', '0')
    pdf.cell(0, 12, f'VALOR DO CONSERTO: R$ {valor}', border=0, ln=True, fill=True, align='C')
    pdf.ln(5)

    # Termo de Garantia
    pdf.set_fill_color(240, 245, 255)
    pdf.set_font('Helvetica', 'B', 12)
    pdf.set_text_color(37, 99, 235)
    pdf.cell(0, 10, 'TERMO DE GARANTIA', border=0, ln=True, fill=True)
    pdf.ln(2)

    pdf.set_font('Helvetica', '', 10)
    pdf.set_text_color(0, 0, 0)
    pdf.multi_cell(0, 7, str(order_data.get('termoGarantia', 'Garantia de 90 dias conforme legislação vigente.')))
    pdf.ln(10)

    # Assinaturas
    pdf.set_font('Helvetica', 'B', 11)
    pdf.set_text_color(37, 99, 235)
    pdf.cell(0, 8, 'ASSINATURAS', border=0, ln=True)
    pdf.ln(15)

    pdf.set_draw_color(100, 100, 100)
    pdf.set_text_color(0, 0, 0)
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
