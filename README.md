# 📝 TaskBeats - Lista de Tarefas por Categorias

Este é um aplicativo Android Nativo desenvolvido em Kotlin que permite ao usuário organizar suas tarefas de maneira eficiente, categorizando-as e visualizando tudo de forma clara.
A interface é simples, intuitiva e adaptada para criar, editar e excluir tarefas e categorias com apenas alguns toques.
O app utiliza banco de dados local com Room para armazenar todas as informações e oferece feedback visual com Snackbars e BottomSheets personalizados.

## :camera_flash: Screenshots
<!-- You can add more screenshots here if you like -->
<img src="https://github.com/user-attachments/assets/23cac793-9c35-4742-aaed-722815fd06d9" width=200/> <img src="https://github.com/user-attachments/assets/e944da04-ade4-424f-8306-6861f644d611" width=200/> <img src="https://github.com/user-attachments/assets/687bb83d-30b0-4dce-95ee-7a93835ee179" width=200/> <img src="https://github.com/user-attachments/assets/08a1b2b4-240c-4d96-986c-86f13d6d71a8" width=200/> <img src="https://github.com/user-attachments/assets/9b2f8994-c2dd-44ee-8739-5ce1aa646292" width=200/> <img src="https://github.com/user-attachments/assets/0adfd957-350d-4f4b-9620-e0a5dca43c84" width=200/> <img src="https://github.com/user-attachments/assets/342eb87e-600c-4000-9752-bdd33ee76d61" width=200/> <img src="https://github.com/user-attachments/assets/5fe0477e-f6b3-49d8-a933-ed1568f66a6b" width=200/> <img src="https://github.com/user-attachments/assets/cc867018-ddb3-4902-9902-6772751e1dd4" width=200/>


# 🧰 Tecnologias Utilizadas
## 📦 Componentes e Recursos Android
- Activity
- RecyclerView
- Snackbar
- BottomSheetDialogFragment
- FloatingActionButton
- Layouts de UI:
  - ConstraintLayout
  - LinearLayout
- Componentes de UI:
  - TextView
  - ImageView
  - Button
  - TextInputEditText
  - Spinner

## 💾 Persistência de Dados com Room
- Entidades (@Entity)
  - CategoryEntity
  - TaskEntity
- DAOs (@Dao)
  - CategoryDao
  - TaskDao
- Database (@Database)
  - TaskBeatDataBase

# 🧠 Lógica e Processamento
- Criação e edição de categorias via BottomSheet;
- Criação, edição e exclusão de tarefas com validação de campos;
- Filtro de tarefas por categoria com visualização dinâmica;
- Exibição de mensagens informativas com InfoBottomSheet;
- Uso do lifecycleScope com Dispatchers.IO e Dispatchers.Main para chamadas ao banco de dados;
- Interface adaptada para estados vazios (sem categorias ou sem tarefas);
- Atualização automática da UI ao inserir/editar/deletar dados;
- Uso de DiffUtil no ListAdapter para melhorar performance das listas

## License
```
The MIT License (MIT)

Copyright (c) 2025 André Brito Vita

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

