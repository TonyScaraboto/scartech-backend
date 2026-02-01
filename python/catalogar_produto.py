import sys, json

def catalogar_acessorio(acessorio):
    with open('produtos_db.json', 'r+', encoding='utf-8') as f:
        db = json.load(f)
        acessorios = db.get('acessorios', [])
        acessorios.append(acessorio)
        db['acessorios'] = acessorios
        f.seek(0)
        json.dump(db, f, ensure_ascii=False, indent=2)
        f.truncate()
    return 'Acessório catalogado com sucesso'

if __name__ == "__main__":
    acessorio = json.loads(sys.argv[1])
    print(catalogar_acessorio(acessorio))
