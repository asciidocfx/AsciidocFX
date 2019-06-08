Asciidoctor::Extensions.register do
  block do
    named :blok
    on_context :literal
    parse_content_as :literal

    process do |parent, reader, attrs|
      chart_type = attrs[2]

      create_image_block parent, attrs
    end
  end
end