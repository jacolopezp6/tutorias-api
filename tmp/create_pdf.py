from pathlib import Path
import re
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Preformatted, PageBreak
from xml.sax.saxutils import escape

root = Path(__file__).resolve().parents[1]
source = root / "docs" / "especificacion-api.md"
dest = root / "output" / "pdf" / "especificacion-api.pdf"
dest.parent.mkdir(parents=True, exist_ok=True)
text = source.read_text(encoding="utf-8").splitlines()
styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="DocTitle", parent=styles["Title"], fontName="Helvetica-Bold", fontSize=20, leading=25, textColor=colors.HexColor("#17365D"), alignment=TA_CENTER, spaceAfter=16))
styles.add(ParagraphStyle(name="H1x", parent=styles["Heading1"], fontSize=15, leading=19, textColor=colors.HexColor("#17365D"), spaceBefore=12, spaceAfter=7, keepWithNext=True))
styles.add(ParagraphStyle(name="H2x", parent=styles["Heading2"], fontSize=11.5, leading=15, textColor=colors.HexColor("#24527A"), spaceBefore=9, spaceAfter=5, keepWithNext=True))
styles.add(ParagraphStyle(name="Bodyx", parent=styles["BodyText"], fontSize=9, leading=13, spaceAfter=6))
styles.add(ParagraphStyle(name="Cellx", parent=styles["BodyText"], fontSize=7.4, leading=9.5))
styles.add(ParagraphStyle(name="CodeBlockx", fontName="Courier", fontSize=7.5, leading=10, backColor=colors.HexColor("#F3F6F9"), borderPadding=7, leftIndent=5, rightIndent=5, spaceBefore=3, spaceAfter=8))

def inline(s):
    s = escape(s)
    s = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", s)
    s = re.sub(r"`(.+?)`", r"<font name='Courier'>\1</font>", s)
    return s

story=[]; i=0
while i < len(text):
    line=text[i]
    if not line.strip(): i+=1; continue
    if line.startswith("```"):
        i+=1; block=[]
        while i<len(text) and not text[i].startswith("```"):
            block.append(text[i]); i+=1
        story.append(Preformatted("\n".join(block), styles["CodeBlockx"], maxLineLength=95)); i+=1; continue
    if line.startswith("| "):
        rows=[]
        while i<len(text) and text[i].startswith("|"):
            cells=[c.strip() for c in text[i].strip().strip("|").split("|")]
            if not all(re.fullmatch(r"[-: ]+",c or " ") for c in cells):
                rows.append([Paragraph(inline(c),styles["Cellx"]) for c in cells])
            i+=1
        if rows:
            col_count=len(rows[0]); widths=[(A4[0]-36*mm)/col_count]*col_count
            table=Table(rows, colWidths=widths, repeatRows=1, hAlign="LEFT")
            table.setStyle(TableStyle([
                ("BACKGROUND",(0,0),(-1,0),colors.HexColor("#17365D")),("TEXTCOLOR",(0,0),(-1,0),colors.white),
                ("FONTNAME",(0,0),(-1,0),"Helvetica-Bold"),("GRID",(0,0),(-1,-1),0.35,colors.HexColor("#B8C5D1")),
                ("VALIGN",(0,0),(-1,-1),"TOP"),("LEFTPADDING",(0,0),(-1,-1),5),("RIGHTPADDING",(0,0),(-1,-1),5),
                ("TOPPADDING",(0,0),(-1,-1),5),("BOTTOMPADDING",(0,0),(-1,-1),5),
                ("ROWBACKGROUNDS",(0,1),(-1,-1),[colors.white,colors.HexColor("#F3F6F9")])]))
            story.extend([table,Spacer(1,7)])
        continue
    if line.startswith("# "):
        story.append(Paragraph(inline(line[2:]),styles["DocTitle"]))
    elif line.startswith("## "):
        story.append(Paragraph(inline(line[3:]),styles["H1x"]))
    elif line.startswith("### "):
        story.append(Paragraph(inline(line[4:]),styles["H2x"]))
    elif line.startswith("- "):
        story.append(Paragraph("&#8226;&nbsp; "+inline(line[2:]),styles["Bodyx"]))
    else:
        story.append(Paragraph(inline(line),styles["Bodyx"]))
    i+=1

def footer(canvas, doc):
    canvas.saveState(); w,h=A4
    canvas.setStrokeColor(colors.HexColor("#D8E0E8")); canvas.line(18*mm,15*mm,w-18*mm,15*mm)
    canvas.setFont("Helvetica",8); canvas.setFillColor(colors.HexColor("#64748B"))
    canvas.drawString(18*mm,10*mm,"Especificación de API - Aplicativo de tutorías")
    canvas.drawRightString(w-18*mm,10*mm,f"Página {doc.page}")
    canvas.restoreState()

doc=SimpleDocTemplate(str(dest),pagesize=A4,rightMargin=18*mm,leftMargin=18*mm,topMargin=17*mm,bottomMargin=22*mm,title="Especificación de recursos y DTOs - API de tutorías",author="")
doc.build(story,onFirstPage=footer,onLaterPages=footer)
print(dest)
